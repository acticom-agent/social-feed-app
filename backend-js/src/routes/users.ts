import { Router } from 'express';
import prisma from '../prisma';
import { AuthRequest, authMiddleware } from '../middleware/auth';
import { AppError } from '../middleware/error';

const router = Router();

router.get('/:id', async (req, res, next) => {
  try {
    const user = await prisma.user.findUnique({
      where: { id: req.params.id },
      select: { id: true, username: true, avatarUrl: true, createdAt: true },
    });
    if (!user) throw new AppError(404, 'User not found');
    res.json(user);
  } catch (err) { next(err); }
});

router.put('/me', authMiddleware, async (req: AuthRequest, res, next) => {
  try {
    const { username, avatarUrl } = req.body;
    const data: any = {};
    if (username !== undefined) {
      if (!username) throw new AppError(400, 'Username cannot be empty');
      const existing = await prisma.user.findUnique({ where: { username } });
      if (existing && existing.id !== req.userId) throw new AppError(409, 'Username already taken');
      data.username = username;
    }
    if (avatarUrl !== undefined) data.avatarUrl = avatarUrl;

    const user = await prisma.user.update({
      where: { id: req.userId },
      select: { id: true, username: true, avatarUrl: true, createdAt: true },
      data,
    });
    res.json(user);
  } catch (err) { next(err); }
});

export default router;
