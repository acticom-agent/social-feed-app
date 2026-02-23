package com.example.socialfeed.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.socialfeed.data.db.dao.CommentDao;
import com.example.socialfeed.data.db.dao.CommentDao_Impl;
import com.example.socialfeed.data.db.dao.LikeDao;
import com.example.socialfeed.data.db.dao.LikeDao_Impl;
import com.example.socialfeed.data.db.dao.PostDao;
import com.example.socialfeed.data.db.dao.PostDao_Impl;
import com.example.socialfeed.data.db.dao.UserDao;
import com.example.socialfeed.data.db.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SocialFeedDatabase_Impl extends SocialFeedDatabase {
  private volatile UserDao _userDao;

  private volatile PostDao _postDao;

  private volatile LikeDao _likeDao;

  private volatile CommentDao _commentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `username` TEXT NOT NULL, `avatarPath` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `posts` (`id` TEXT NOT NULL, `authorId` TEXT NOT NULL, `text` TEXT NOT NULL, `imagePath` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`authorId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_authorId` ON `posts` (`authorId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `likes` (`postId` TEXT NOT NULL, `userId` TEXT NOT NULL, PRIMARY KEY(`postId`, `userId`), FOREIGN KEY(`postId`) REFERENCES `posts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_likes_postId` ON `likes` (`postId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_likes_userId` ON `likes` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `comments` (`id` TEXT NOT NULL, `postId` TEXT NOT NULL, `authorId` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`postId`) REFERENCES `posts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`authorId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_postId` ON `comments` (`postId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_authorId` ON `comments` (`authorId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fa6fd8d817c7d9e46377b6306913e0d3')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `posts`");
        db.execSQL("DROP TABLE IF EXISTS `likes`");
        db.execSQL("DROP TABLE IF EXISTS `comments`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(3);
        _columnsUsers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("avatarPath", new TableInfo.Column("avatarPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.example.socialfeed.data.db.entity.User).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsPosts = new HashMap<String, TableInfo.Column>(5);
        _columnsPosts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosts.put("authorId", new TableInfo.Column("authorId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosts.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosts.put("imagePath", new TableInfo.Column("imagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPosts = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPosts.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("authorId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPosts = new HashSet<TableInfo.Index>(1);
        _indicesPosts.add(new TableInfo.Index("index_posts_authorId", false, Arrays.asList("authorId"), Arrays.asList("ASC")));
        final TableInfo _infoPosts = new TableInfo("posts", _columnsPosts, _foreignKeysPosts, _indicesPosts);
        final TableInfo _existingPosts = TableInfo.read(db, "posts");
        if (!_infoPosts.equals(_existingPosts)) {
          return new RoomOpenHelper.ValidationResult(false, "posts(com.example.socialfeed.data.db.entity.Post).\n"
                  + " Expected:\n" + _infoPosts + "\n"
                  + " Found:\n" + _existingPosts);
        }
        final HashMap<String, TableInfo.Column> _columnsLikes = new HashMap<String, TableInfo.Column>(2);
        _columnsLikes.put("postId", new TableInfo.Column("postId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLikes.put("userId", new TableInfo.Column("userId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLikes = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysLikes.add(new TableInfo.ForeignKey("posts", "CASCADE", "NO ACTION", Arrays.asList("postId"), Arrays.asList("id")));
        _foreignKeysLikes.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesLikes = new HashSet<TableInfo.Index>(2);
        _indicesLikes.add(new TableInfo.Index("index_likes_postId", false, Arrays.asList("postId"), Arrays.asList("ASC")));
        _indicesLikes.add(new TableInfo.Index("index_likes_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoLikes = new TableInfo("likes", _columnsLikes, _foreignKeysLikes, _indicesLikes);
        final TableInfo _existingLikes = TableInfo.read(db, "likes");
        if (!_infoLikes.equals(_existingLikes)) {
          return new RoomOpenHelper.ValidationResult(false, "likes(com.example.socialfeed.data.db.entity.Like).\n"
                  + " Expected:\n" + _infoLikes + "\n"
                  + " Found:\n" + _existingLikes);
        }
        final HashMap<String, TableInfo.Column> _columnsComments = new HashMap<String, TableInfo.Column>(5);
        _columnsComments.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("postId", new TableInfo.Column("postId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("authorId", new TableInfo.Column("authorId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysComments = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysComments.add(new TableInfo.ForeignKey("posts", "CASCADE", "NO ACTION", Arrays.asList("postId"), Arrays.asList("id")));
        _foreignKeysComments.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("authorId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesComments = new HashSet<TableInfo.Index>(2);
        _indicesComments.add(new TableInfo.Index("index_comments_postId", false, Arrays.asList("postId"), Arrays.asList("ASC")));
        _indicesComments.add(new TableInfo.Index("index_comments_authorId", false, Arrays.asList("authorId"), Arrays.asList("ASC")));
        final TableInfo _infoComments = new TableInfo("comments", _columnsComments, _foreignKeysComments, _indicesComments);
        final TableInfo _existingComments = TableInfo.read(db, "comments");
        if (!_infoComments.equals(_existingComments)) {
          return new RoomOpenHelper.ValidationResult(false, "comments(com.example.socialfeed.data.db.entity.Comment).\n"
                  + " Expected:\n" + _infoComments + "\n"
                  + " Found:\n" + _existingComments);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "fa6fd8d817c7d9e46377b6306913e0d3", "2898ea14f0b2c51b720244738266b809");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","posts","likes","comments");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `posts`");
      _db.execSQL("DELETE FROM `likes`");
      _db.execSQL("DELETE FROM `comments`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PostDao.class, PostDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LikeDao.class, LikeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CommentDao.class, CommentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public PostDao postDao() {
    if (_postDao != null) {
      return _postDao;
    } else {
      synchronized(this) {
        if(_postDao == null) {
          _postDao = new PostDao_Impl(this);
        }
        return _postDao;
      }
    }
  }

  @Override
  public LikeDao likeDao() {
    if (_likeDao != null) {
      return _likeDao;
    } else {
      synchronized(this) {
        if(_likeDao == null) {
          _likeDao = new LikeDao_Impl(this);
        }
        return _likeDao;
      }
    }
  }

  @Override
  public CommentDao commentDao() {
    if (_commentDao != null) {
      return _commentDao;
    } else {
      synchronized(this) {
        if(_commentDao == null) {
          _commentDao = new CommentDao_Impl(this);
        }
        return _commentDao;
      }
    }
  }
}
