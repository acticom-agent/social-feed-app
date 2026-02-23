import { Router } from 'express';
import prisma from '../prisma';
import { AuthRequest, authMiddleware } from '../middleware/auth';
import { AppError } from '../middleware/error';

const router = Router();

router.delete('/:id', authMiddleware, async (req: AuthRequest, res, next) => {
  try {
    const comment = await prisma.comment.findUnique({ where: { id: req.params.id } });
    if (!comment) throw new AppError(404, 'Comment not found');
    if (comment.authorId !== req.userId) throw new AppError(403, 'Not authorized');

    await prisma.comment.delete({ where: { id: req.params.id } });
    res.json({ message: 'Comment deleted' });
  } catch (err) { next(err); }
});

export default router;
