package com.example.socialfeed.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.socialfeed.data.db.entity.Post;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PostDao_Impl implements PostDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Post> __insertionAdapterOfPost;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public PostDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPost = new EntityInsertionAdapter<Post>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `posts` (`id`,`authorId`,`text`,`imagePath`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Post entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getAuthorId());
        statement.bindString(3, entity.getText());
        if (entity.getImagePath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getImagePath());
        }
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM posts";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Post post, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPost.insert(post);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id, final Continuation<? super Post> $completion) {
    final String _sql = "SELECT * FROM posts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Post>() {
      @Override
      @Nullable
      public Post call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAuthorId = CursorUtil.getColumnIndexOrThrow(_cursor, "authorId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Post _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAuthorId;
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new Post(_tmpId,_tmpAuthorId,_tmpText,_tmpImagePath,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PostWithDetails>> getFeedPaginated(final int limit, final int offset) {
    final String _sql = "\n"
            + "        SELECT p.id, p.authorId, p.text, p.imagePath, p.createdAt,\n"
            + "               u.username, u.avatarPath,\n"
            + "               (SELECT COUNT(*) FROM likes WHERE postId = p.id) as likeCount,\n"
            + "               (SELECT COUNT(*) FROM comments WHERE postId = p.id) as commentCount\n"
            + "        FROM posts p\n"
            + "        INNER JOIN users u ON p.authorId = u.id\n"
            + "        ORDER BY p.createdAt DESC\n"
            + "        LIMIT ? OFFSET ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, offset);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"likes", "comments", "posts",
        "users"}, new Callable<List<PostWithDetails>>() {
      @Override
      @NonNull
      public List<PostWithDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfAuthorId = 1;
          final int _cursorIndexOfText = 2;
          final int _cursorIndexOfImagePath = 3;
          final int _cursorIndexOfCreatedAt = 4;
          final int _cursorIndexOfUsername = 5;
          final int _cursorIndexOfAvatarPath = 6;
          final int _cursorIndexOfLikeCount = 7;
          final int _cursorIndexOfCommentCount = 8;
          final List<PostWithDetails> _result = new ArrayList<PostWithDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PostWithDetails _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAuthorId;
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpAvatarPath;
            if (_cursor.isNull(_cursorIndexOfAvatarPath)) {
              _tmpAvatarPath = null;
            } else {
              _tmpAvatarPath = _cursor.getString(_cursorIndexOfAvatarPath);
            }
            final int _tmpLikeCount;
            _tmpLikeCount = _cursor.getInt(_cursorIndexOfLikeCount);
            final int _tmpCommentCount;
            _tmpCommentCount = _cursor.getInt(_cursorIndexOfCommentCount);
            _item = new PostWithDetails(_tmpId,_tmpAuthorId,_tmpText,_tmpImagePath,_tmpCreatedAt,_tmpUsername,_tmpAvatarPath,_tmpLikeCount,_tmpCommentCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<PostWithDetails> getPostWithDetails(final String postId) {
    final String _sql = "\n"
            + "        SELECT p.id, p.authorId, p.text, p.imagePath, p.createdAt,\n"
            + "               u.username, u.avatarPath,\n"
            + "               (SELECT COUNT(*) FROM likes WHERE postId = p.id) as likeCount,\n"
            + "               (SELECT COUNT(*) FROM comments WHERE postId = p.id) as commentCount\n"
            + "        FROM posts p\n"
            + "        INNER JOIN users u ON p.authorId = u.id\n"
            + "        WHERE p.id = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, postId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"likes", "comments", "posts",
        "users"}, new Callable<PostWithDetails>() {
      @Override
      @Nullable
      public PostWithDetails call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfAuthorId = 1;
          final int _cursorIndexOfText = 2;
          final int _cursorIndexOfImagePath = 3;
          final int _cursorIndexOfCreatedAt = 4;
          final int _cursorIndexOfUsername = 5;
          final int _cursorIndexOfAvatarPath = 6;
          final int _cursorIndexOfLikeCount = 7;
          final int _cursorIndexOfCommentCount = 8;
          final PostWithDetails _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAuthorId;
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpAvatarPath;
            if (_cursor.isNull(_cursorIndexOfAvatarPath)) {
              _tmpAvatarPath = null;
            } else {
              _tmpAvatarPath = _cursor.getString(_cursorIndexOfAvatarPath);
            }
            final int _tmpLikeCount;
            _tmpLikeCount = _cursor.getInt(_cursorIndexOfLikeCount);
            final int _tmpCommentCount;
            _tmpCommentCount = _cursor.getInt(_cursorIndexOfCommentCount);
            _result = new PostWithDetails(_tmpId,_tmpAuthorId,_tmpText,_tmpImagePath,_tmpCreatedAt,_tmpUsername,_tmpAvatarPath,_tmpLikeCount,_tmpCommentCount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PostWithDetails>> getPostsByUser(final String userId) {
    final String _sql = "\n"
            + "        SELECT p.id, p.authorId, p.text, p.imagePath, p.createdAt,\n"
            + "               u.username, u.avatarPath,\n"
            + "               (SELECT COUNT(*) FROM likes WHERE postId = p.id) as likeCount,\n"
            + "               (SELECT COUNT(*) FROM comments WHERE postId = p.id) as commentCount\n"
            + "        FROM posts p\n"
            + "        INNER JOIN users u ON p.authorId = u.id\n"
            + "        WHERE p.authorId = ?\n"
            + "        ORDER BY p.createdAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"likes", "comments", "posts",
        "users"}, new Callable<List<PostWithDetails>>() {
      @Override
      @NonNull
      public List<PostWithDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfAuthorId = 1;
          final int _cursorIndexOfText = 2;
          final int _cursorIndexOfImagePath = 3;
          final int _cursorIndexOfCreatedAt = 4;
          final int _cursorIndexOfUsername = 5;
          final int _cursorIndexOfAvatarPath = 6;
          final int _cursorIndexOfLikeCount = 7;
          final int _cursorIndexOfCommentCount = 8;
          final List<PostWithDetails> _result = new ArrayList<PostWithDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PostWithDetails _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAuthorId;
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpAvatarPath;
            if (_cursor.isNull(_cursorIndexOfAvatarPath)) {
              _tmpAvatarPath = null;
            } else {
              _tmpAvatarPath = _cursor.getString(_cursorIndexOfAvatarPath);
            }
            final int _tmpLikeCount;
            _tmpLikeCount = _cursor.getInt(_cursorIndexOfLikeCount);
            final int _tmpCommentCount;
            _tmpCommentCount = _cursor.getInt(_cursorIndexOfCommentCount);
            _item = new PostWithDetails(_tmpId,_tmpAuthorId,_tmpText,_tmpImagePath,_tmpCreatedAt,_tmpUsername,_tmpAvatarPath,_tmpLikeCount,_tmpCommentCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<Post>> $completion) {
    final String _sql = "SELECT * FROM posts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Post>>() {
      @Override
      @NonNull
      public List<Post> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAuthorId = CursorUtil.getColumnIndexOrThrow(_cursor, "authorId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Post> _result = new ArrayList<Post>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Post _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAuthorId;
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Post(_tmpId,_tmpAuthorId,_tmpText,_tmpImagePath,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
