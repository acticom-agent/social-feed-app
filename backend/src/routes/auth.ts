import { Router } from 'express';
import bcrypt from 'bcryptjs';
import prisma from '../prisma';
import { AuthRequest, authMiddleware, generateToken } from '../middleware/auth';
import { AppError } from '../middleware/error';

const router = Router();

router.post('/register', async (req, res, next) => {
  try {
    const { username, password } = req.body;
    if (!username || !password) throw new AppError(400, 'Username and password required');
    if (password.length < 4) throw new AppError(400, 'Password must be at least 4 characters');

    const existing = await prisma.user.findUnique({ where: { username } });
    if (existing) throw new AppError(409, 'Username already taken');

    const passwordHash = await bcrypt.hash(password, 10);
    const user = await prisma.user.create({
      data: { username, passwordHash },
    });

    const token = generateToken(user.id);
    res.status(201).json({ token, user: { id: user.id, username: user.username, avatarUrl: user.avatarUrl, createdAt: user.createdAt } });
  } catch (err) { next(err); }
});

router.post('/login', async (req, res, next) => {
  try {
    const { username, password } = req.body;
    if (!username || !password) throw new AppError(400, 'Username and password required');

    const user = await prisma.user.findUnique({ where: { username } });
    if (!user) throw new AppError(401, 'Invalid credentials');

    const valid = await bcrypt.compare(password, user.passwordHash);
    if (!valid) throw new AppError(401, 'Invalid credentials');

    const token = generateToken(user.id);
    res.json({ token, user: { id: user.id, username: user.username, avatarUrl: user.avatarUrl, createdAt: user.createdAt } });
  } catch (err) { next(err); }
});

router.get('/me', authMiddleware, async (req: AuthRequest, res, next) => {
  try {
    const user = await prisma.user.findUnique({
      where: { id: req.userId },
      select: { id: true, username: true, avatarUrl: true, createdAt: true },
    });
    if (!user) throw new AppError(404, 'User not found');
    res.json(user);
  } catch (err) { next(err); }
});

export default router;
