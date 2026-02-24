@echo off
REM Start Social Feed Backend (JS)

echo === Starting Social Feed Backend (JS) ===
cd /d "%~dp0backend-js"
call npm install --silent 2>nul
call npx prisma generate --schema=prisma/schema.prisma 2>nul
call npx prisma db push --schema=prisma/schema.prisma 2>nul
echo Starting server on http://localhost:3000...
call npm run dev
