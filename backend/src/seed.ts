import prisma from './prisma';
import bcrypt from 'bcryptjs';

async function seed() {
  console.log('Seeding database...');

  await prisma.comment.deleteMany();
  await prisma.like.deleteMany();
  await prisma.post.deleteMany();
  await prisma.user.deleteMany();

  const hash = await bcrypt.hash('password', 10);

  const alice = await prisma.user.create({
    data: { username: 'alice', passwordHash: hash, avatarUrl: 'https://i.pravatar.cc/150?u=alice' },
  });
  const bob = await prisma.user.create({
    data: { username: 'bob', passwordHash: hash, avatarUrl: 'https://i.pravatar.cc/150?u=bob' },
  });
  const charlie = await prisma.user.create({
    data: { username: 'charlie', passwordHash: hash, avatarUrl: 'https://i.pravatar.cc/150?u=charlie' },
  });

  const post1 = await prisma.post.create({
    data: { text: 'Hello world! This is my first post 🌍', authorId: alice.id },
  });
  const post2 = await prisma.post.create({
    data: { text: 'Beautiful sunset today 🌅', imageUrl: 'https://picsum.photos/seed/sunset/800/600', authorId: bob.id },
  });
  const post3 = await prisma.post.create({
    data: { text: 'Just shipped a new feature! 🚀', authorId: charlie.id },
  });

  await prisma.like.create({ data: { postId: post1.id, userId: bob.id } });
  await prisma.like.create({ data: { postId: post1.id, userId: charlie.id } });
  await prisma.like.create({ data: { postId: post2.id, userId: alice.id } });

  await prisma.comment.create({ data: { text: 'Welcome! 🎉', postId: post1.id, authorId: bob.id } });
  await prisma.comment.create({ data: { text: 'Amazing photo!', postId: post2.id, authorId: alice.id } });
  await prisma.comment.create({ data: { text: 'Congrats! 🎊', postId: post3.id, authorId: alice.id } });
  await prisma.comment.create({ data: { text: 'Well done!', postId: post3.id, authorId: bob.id } });

  console.log('Seeded: 3 users, 3 posts, 3 likes, 4 comments');
  console.log('Login with username: alice/bob/charlie, password: password');
}

seed()
  .catch(console.error)
  .finally(() => prisma.$disconnect());
