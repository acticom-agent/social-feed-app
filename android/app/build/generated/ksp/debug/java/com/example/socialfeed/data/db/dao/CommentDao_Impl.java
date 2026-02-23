package com.example.socialfeed.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.socialfeed.data.db.entity.Comment;
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
public final class CommentDao_Impl implements CommentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Comment> __insertionAdapterOfComment;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public CommentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfComment = new EntityInsertionAdapter<Comment>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `comments` (`id`,`postId`,`authorId`,`text`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Comment entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPostId());
        statement.bindString(3, entity.getAuthorId());
        statement.bindString(4, entity.getText());
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM comments";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Comment comment, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfComment.insert(comment);
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
  public Flow<List<CommentWithUser>> getCommentsForPost(final String postId) {
    final String _sql = "\n"
            + "        SELECT c.id, c.postId, c.authorId, c.text, c.createdAt,\n"
            + "               u.username, u.avatarPath\n"
            + "        FROM comments c\n"
            + "        INNER JOIN users u ON c.authorId = u.id\n"
            + "        WHERE c.postId = ?\n"
            + "        ORDER BY c.createdAt ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, postId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"comments",
        "users"}, new Callable<List<CommentWithUser>>() {
      @Override
      @NonNull
      public List<CommentWithUser> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfPostId = 1;
          final int _cursorIndexOfAuthorId = 2;
          final int _cursorIndexOfText = 3;
          final int _cursorIndexOfCreatedAt = 4;
          final int _cursorIndexOfUsername = 5;
          final int _cursorIndexOfAvatarPath = 6;
          final List<CommentWithUser> _result = new ArrayList<CommentWithUser>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CommentWithUser _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPostId;
            _tmpPostId = _cursor.getString(_cursorIndexOfPostId);
            final String _tmpAuthorId;
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
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
            _item = new CommentWithUser(_tmpId,_tmpPostId,_tmpAuthorId,_tmpText,_tmpCreatedAt,_tmpUsername,_tmpAvatarPath);
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
  public Object getAll(final Continuation<? super List<Comment>> $completion) {
    final String _sql = "SELECT * FROM comments";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Comment>>() {
      @Override
      @NonNull
      public List<Comment> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "postId");
          final int _cursorIndexOfAuthorId = CursorUtil.getColumnIndexOrThrow(_cursor, "authorId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Comment> _result = new ArrayList<Comment>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Comment _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPostId;
            _tmpPostId = _cursor.getString(_cursorIndexOfPostId);
            final String _tmpAuthorId;
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Comment(_tmpId,_tmpPostId,_tmpAuthorId,_tmpText,_tmpCreatedAt);
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
