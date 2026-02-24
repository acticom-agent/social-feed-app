#!/bin/bash
# Start Social Feed backends
set -e

echo "=== Starting Social Feed Backend (JS) ==="
cd "$(dirname "$0")/backend-js"
npm install --silent 2>/dev/null
npx prisma generate --schema=prisma/schema.prisma 2>/dev/null
npx prisma db push --schema=prisma/schema.prisma 2>/dev/null
echo "Starting server on http://localhost:3000..."
npm run dev
