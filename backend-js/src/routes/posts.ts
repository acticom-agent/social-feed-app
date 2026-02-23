import { Router } from 'express';
import prisma from '../prisma';
import { AuthRequest, authMiddleware, optionalAuth } from '../middleware/auth';
import { AppError } from '../middleware/error';
import { upload } from '../middleware/upload';

const router = Router();

router.get('/', optionalAuth, async (req: AuthRequest, res, next) => {
  try {
    const limit = Math.min(parseInt(req.query.limit as string) || 20, 100);
    const offset = parseInt(req.query.offset as string) || 0;

    const posts = await prisma.post.findMany({
      take: limit,
      skip: offset,
      orderBy: { createdAt: 'desc' },
      include: {
        author: { select: { id: true, username: true, avatarUrl: true } },
        _count: { select: { likes: true, comments: true } },
      },
    });

    const total = await prisma.post.count();
    res.json({ posts, total, limit, offset });
  } catch (err) { next(err); }
});

router.get('/:id', optionalAuth, async (req: AuthRequest, res, next) => {
  try {
    const post = await prisma.post.findUnique({
      where: { id: req.params.id },
      include: {
        author: { select: { id: true, username: true, avatarUrl: true } },
        comments: {
          orderBy: { createdAt: 'asc' },
          include: { author: { select: { id: true, username: true, avatarUrl: true } } },
        },
        _count: { select: { likes: true } },
      },
    });
    if (!post) throw new AppError(404, 'Post not found');
    res.json(post);
  } catch (err) { next(err); }
});

router.post('/', authMiddleware, upload.single('image'), async (req: AuthRequest, res, next) => {
  try {
    const { text } = req.body;
    if (!text?.trim()) throw new AppError(400, 'Text is required');

    const imageUrl = req.file ? `/uploads/${req.file.filename}` : undefined;
    const post = await prisma.post.create({
      data: { text: text.trim(), imageUrl, authorId: req.userId! },
      include: { author: { select: { id: true, username: true, avatarUrl: true } } },
    });
    res.status(201).json(post);
  } catch (err) { next(err); }
});

router.delete('/:id', authMiddleware, async (req: AuthRequest, res, next) => {
  try {
    const post = await prisma.post.findUnique({ where: { id: req.params.id } });
    if (!post) throw new AppError(404, 'Post not found');
    if (post.authorId !== req.userId) throw new AppError(403, 'Not authorized');

    await prisma.post.delete({ where: { id: req.params.id } });
    res.json({ message: 'Post deleted' });
  } catch (err) { next(err); }
});

// Likes
router.post('/:id/like', authMiddleware, async (req: AuthRequest, res, next) => {
  try {
    const postId = req.params.id;
    const post = await prisma.post.findUnique({ where: { id: postId } });
    if (!post) throw new AppError(404, 'Post not found');

    const existing = await prisma.like.findUnique({
      where: { postId_userId: { postId, userId: req.userId! } },
    });

    if (existing) {
      await prisma.like.delete({ where: { id: existing.id } });
      res.json({ liked: false });
    } else {
      await prisma.like.create({ data: { postId, userId: req.userId! } });
      res.json({ liked: true });
    }
  } catch (err) { next(err); }
});

router.get('/:id/likes', optionalAuth, async (req: AuthRequest, res, next) => {
  try {
    const postId = req.params.id;
    const count = await prisma.like.count({ where: { postId } });
    let liked = false;
    if (req.userId) {
      const existing = await prisma.like.findUnique({
        where: { postId_userId: { postId, userId: req.userId } },
      });
      liked = !!existing;
    }
    res.json({ count, liked });
  } catch (err) { next(err); }
});

// Comments
router.get('/:id/comments', async (req, res, next) => {
  try {
    const limit = Math.min(parseInt(req.query.limit as string) || 20, 100);
    const offset = parseInt(req.query.offset as string) || 0;

    const comments = await prisma.comment.findMany({
      where: { postId: req.params.id },
      take: limit,
      skip: offset,
      orderBy: { createdAt: 'asc' },
      include: { author: { select: { id: true, username: true, avatarUrl: true } } },
    });
    const total = await prisma.comment.count({ where: { postId: req.params.id } });
    res.json({ comments, total, limit, offset });
  } catch (err) { next(err); }
});

router.post('/:id/comments', authMiddleware, async (req: AuthRequest, res, next) => {
  try {
    const { text } = req.body;
    if (!text?.trim()) throw new AppError(400, 'Text is required');

    const post = await prisma.post.findUnique({ where: { id: req.params.id } });
    if (!post) throw new AppError(404, 'Post not found');

    const comment = await prisma.comment.create({
      data: { text: text.trim(), postId: req.params.id, authorId: req.userId! },
      include: { author: { select: { id: true, username: true, avatarUrl: true } } },
    });
    res.status(201).json(comment);
  } catch (err) { next(err); }
});

export default router;
