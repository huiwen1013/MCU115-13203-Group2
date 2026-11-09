package com.example.findbackapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FindBack.db";
    private static final int DATABASE_VERSION = 1;

    // Users Table
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_EMAIL = "email";

    // Items Table
    public static final String TABLE_ITEMS = "Items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM_USER_ID = "user_id";
    public static final String COLUMN_ITEM_TYPE = "item_type";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_ITEM_DATE = "item_date";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_IMAGE_URI = "image_uri";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Claims Table
    public static final String TABLE_CLAIMS = "Claims";
    public static final String COLUMN_CLAIM_ID = "claim_id";
    public static final String COLUMN_CLAIM_ITEM_ID = "item_id";
    public static final String COLUMN_CLAIMANT_ID = "claimant_id";
    public static final String COLUMN_OWNER_ID = "owner_id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_CLAIM_STATUS = "status";
    public static final String COLUMN_CLAIM_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_NICKNAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT)";

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + " ("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ITEM_USER_ID + " INTEGER, "
                + COLUMN_ITEM_TYPE + " TEXT, "
                + COLUMN_ITEM_NAME + " TEXT, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_COLOR + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_ITEM_DATE + " TEXT, "
                + COLUMN_CONTACT + " TEXT, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + COLUMN_STATUS + " TEXT, "
                + COLUMN_CREATED_AT + " TEXT)";

        String CREATE_CLAIMS_TABLE = "CREATE TABLE " + TABLE_CLAIMS + " ("
                + COLUMN_CLAIM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CLAIM_ITEM_ID + " INTEGER, "
                + COLUMN_CLAIMANT_ID + " INTEGER, "
                + COLUMN_OWNER_ID + " INTEGER, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_CLAIM_STATUS + " TEXT, "
                + COLUMN_CLAIM_CREATED_AT + " TEXT)";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_CLAIMS_TABLE);

        seedSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLAIMS);
        onCreate(db);
    }

    private void seedSampleData(SQLiteDatabase db) {
        String now = getCurrentDateTime();

        // Add sample users
        ContentValues user1 = new ContentValues();
        user1.put(COLUMN_USERNAME, "admin");
        user1.put(COLUMN_PASSWORD, "123456");
        user1.put(COLUMN_NICKNAME, "管理員");
        user1.put(COLUMN_EMAIL, "admin@findback.com");
        long u1 = db.insert(TABLE_USERS, null, user1);

        ContentValues user2 = new ContentValues();
        user2.put(COLUMN_USERNAME, "student");
        user2.put(COLUMN_PASSWORD, "123456");
        user2.put(COLUMN_NICKNAME, "小明");
        user2.put(COLUMN_EMAIL, "student@example.com");
        long u2 = db.insert(TABLE_USERS, null, user2);

        // Add sample items (Lost and Found)
        ContentValues item1 = new ContentValues();
        item1.put(COLUMN_ITEM_USER_ID, u2);
        item1.put(COLUMN_ITEM_TYPE, Item.TYPE_LOST);
        item1.put(COLUMN_ITEM_NAME, "黑色 iPhone 15 Pro");
        item1.put(COLUMN_CATEGORY, "手機／平板");
        item1.put(COLUMN_COLOR, "黑色");
        item1.put(COLUMN_DESCRIPTION, "透明手機殼，背面有卡通貼紙，螢幕右上方有微小刮痕");
        item1.put(COLUMN_LOCATION, "圖書館一樓閱覽室");
        item1.put(COLUMN_ITEM_DATE, "2026-11-05");
        item1.put(COLUMN_CONTACT, "0912-345-678");
        item1.put(COLUMN_IMAGE_URI, "");
        item1.put(COLUMN_STATUS, Item.STATUS_ACTIVE);
        item1.put(COLUMN_CREATED_AT, now);
        db.insert(TABLE_ITEMS, null, item1);

        ContentValues item2 = new ContentValues();
        item2.put(COLUMN_ITEM_USER_ID, u1);
        item2.put(COLUMN_ITEM_TYPE, Item.TYPE_FOUND);
        item2.put(COLUMN_ITEM_NAME, "黑色 iPhone 蘋果手機");
        item2.put(COLUMN_CATEGORY, "手機／平板");
        item2.put(COLUMN_COLOR, "黑色");
        item2.put(COLUMN_DESCRIPTION, "在座位區拾獲黑色智慧型手機，附透明保護殼");
        item2.put(COLUMN_LOCATION, "圖書館");
        item2.put(COLUMN_ITEM_DATE, "2026-11-05");
        item2.put(COLUMN_CONTACT, "0987-654-321");
        item2.put(COLUMN_IMAGE_URI, "");
        item2.put(COLUMN_STATUS, Item.STATUS_ACTIVE);
        item2.put(COLUMN_CREATED_AT, now);
        db.insert(TABLE_ITEMS, null, item2);

        ContentValues item3 = new ContentValues();
        item3.put(COLUMN_ITEM_USER_ID, u2);
        item3.put(COLUMN_ITEM_TYPE, Item.TYPE_LOST);
        item3.put(COLUMN_ITEM_NAME, "藍色長夾皮包");
        item3.put(COLUMN_CATEGORY, "錢包");
        item3.put(COLUMN_COLOR, "藍色");
        item3.put(COLUMN_DESCRIPTION, "內有學生證及幾張信用卡，藍色皮革設計");
        item3.put(COLUMN_LOCATION, "學生餐廳二樓");
        item3.put(COLUMN_ITEM_DATE, "2026-11-04");
        item3.put(COLUMN_CONTACT, "student@example.com");
        item3.put(COLUMN_IMAGE_URI, "");
        item3.put(COLUMN_STATUS, Item.STATUS_ACTIVE);
        item3.put(COLUMN_CREATED_AT, now);
        db.insert(TABLE_ITEMS, null, item3);

        ContentValues item4 = new ContentValues();
        item4.put(COLUMN_ITEM_USER_ID, u1);
        item4.put(COLUMN_ITEM_TYPE, Item.TYPE_FOUND);
        item4.put(COLUMN_ITEM_NAME, "機車鑰匙一串");
        item4.put(COLUMN_CATEGORY, "鑰匙");
        item4.put(COLUMN_COLOR, "銀色");
        item4.put(COLUMN_DESCRIPTION, "三把鑰匙加上一個黑色鑰匙圈");
        item4.put(COLUMN_LOCATION, "體育館門口");
        item4.put(COLUMN_ITEM_DATE, "2026-11-06");
        item4.put(COLUMN_CONTACT, "0911-222-333");
        item4.put(COLUMN_IMAGE_URI, "");
        item4.put(COLUMN_STATUS, Item.STATUS_ACTIVE);
        item4.put(COLUMN_CREATED_AT, now);
        db.insert(TABLE_ITEMS, null, item4);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // --- User Operations ---

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_NICKNAME, user.getNickname());
        values.put(COLUMN_EMAIL, user.getEmail());
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICKNAME, user.getNickname());
        values.put(COLUMN_EMAIL, user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            values.put(COLUMN_PASSWORD, user.getPassword());
        }
        int rows = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(user.getUserId())});
        db.close();
        return rows > 0;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        return user;
    }

    // --- Item Operations ---

    public long addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_USER_ID, item.getUserId());
        values.put(COLUMN_ITEM_TYPE, item.getItemType());
        values.put(COLUMN_ITEM_NAME, item.getItemName());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_COLOR, item.getColor());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_LOCATION, item.getLocation());
        values.put(COLUMN_ITEM_DATE, item.getItemDate());
        values.put(COLUMN_CONTACT, item.getContact());
        values.put(COLUMN_IMAGE_URI, item.getImageUri());
        values.put(COLUMN_STATUS, item.getStatus() != null ? item.getStatus() : Item.STATUS_ACTIVE);
        values.put(COLUMN_CREATED_AT, item.getCreatedAt() != null ? item.getCreatedAt() : getCurrentDateTime());

        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    public List<Item> getAllItems() {
        return getItemsWhere(null, null);
    }

    public List<Item> getItemsByType(String type) {
        return getItemsWhere(COLUMN_ITEM_TYPE + "=?", new String[]{type});
    }

    public List<Item> getItemsByUser(long userId) {
        return getItemsWhere(COLUMN_ITEM_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public List<Item> getItemsByUserAndType(long userId, String type) {
        return getItemsWhere(COLUMN_ITEM_USER_ID + "=? AND " + COLUMN_ITEM_TYPE + "=?",
                new String[]{String.valueOf(userId), type});
    }

    public Item getItemById(long itemId) {
        List<Item> list = getItemsWhere(COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Item> searchItems(String keyword, String category, String color, String location, String date) {
        StringBuilder where = new StringBuilder("1=1");
        List<String> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND (").append(COLUMN_ITEM_NAME).append(" LIKE ? OR ")
                    .append(COLUMN_DESCRIPTION).append(" LIKE ?)");
            args.add("%" + keyword.trim() + "%");
            args.add("%" + keyword.trim() + "%");
        }

        if (category != null && !category.isEmpty() && !category.contains("請選擇")) {
            where.append(" AND ").append(COLUMN_CATEGORY).append("=?");
            args.add(category);
        }

        if (color != null && !color.isEmpty() && !color.contains("請選擇")) {
            where.append(" AND ").append(COLUMN_COLOR).append("=?");
            args.add(color);
        }

        if (location != null && !location.trim().isEmpty()) {
            where.append(" AND ").append(COLUMN_LOCATION).append(" LIKE ?");
            args.add("%" + location.trim() + "%");
        }

        if (date != null && !date.trim().isEmpty()) {
            where.append(" AND ").append(COLUMN_ITEM_DATE).append(" LIKE ?");
            args.add("%" + date.trim() + "%");
        }

        return getItemsWhere(where.toString(), args.toArray(new String[0]));
    }

    public boolean updateItemStatus(long itemId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        int rows = db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
        return rows > 0;
    }

    public boolean deleteItem(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ITEMS, COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
        return rows > 0;
    }

    private List<Item> getItemsWhere(String selection, String[] selectionArgs) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, selection, selectionArgs, null, null, COLUMN_ITEM_ID + " DESC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setItemId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID)));
                    item.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_USER_ID)));
                    item.setItemType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TYPE)));
                    item.setItemName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME)));
                    item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                    item.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR)));
                    item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                    item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                    item.setItemDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DATE)));
                    item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT)));
                    item.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
                    item.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                    item.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                    items.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return items;
    }

    // --- Claim Operations ---

    public long addClaim(Claim claim) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLAIM_ITEM_ID, claim.getItemId());
        values.put(COLUMN_CLAIMANT_ID, claim.getClaimantId());
        values.put(COLUMN_OWNER_ID, claim.getOwnerId());
        values.put(COLUMN_MESSAGE, claim.getMessage());
        values.put(COLUMN_CLAIM_STATUS, claim.getStatus() != null ? claim.getStatus() : Claim.STATUS_PENDING);
        values.put(COLUMN_CLAIM_CREATED_AT, claim.getCreatedAt() != null ? claim.getCreatedAt() : getCurrentDateTime());

        long id = db.insert(TABLE_CLAIMS, null, values);
        db.close();
        return id;
    }

    public boolean hasClaimed(long itemId, long claimantId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLAIMS, new String[]{COLUMN_CLAIM_ID},
                COLUMN_CLAIM_ITEM_ID + "=? AND " + COLUMN_CLAIMANT_ID + "=?",
                new String[]{String.valueOf(itemId), String.valueOf(claimantId)},
                null, null, null);
        boolean claimed = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return claimed;
    }

    public List<Claim> getClaimsByClaimant(long claimantId) {
        return getClaimsWhere(COLUMN_CLAIMANT_ID + "=?", new String[]{String.valueOf(claimantId)});
    }

    public List<Claim> getClaimsByOwner(long ownerId) {
        return getClaimsWhere(COLUMN_OWNER_ID + "=?", new String[]{String.valueOf(ownerId)});
    }

    public boolean updateClaimStatus(long claimId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLAIM_STATUS, status);
        int rows = db.update(TABLE_CLAIMS, values, COLUMN_CLAIM_ID + "=?", new String[]{String.valueOf(claimId)});
        db.close();
        return rows > 0;
    }

    private List<Claim> getClaimsWhere(String selection, String[] selectionArgs) {
        List<Claim> claims = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLAIMS, null, selection, selectionArgs, null, null, COLUMN_CLAIM_ID + " DESC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Claim claim = new Claim();
                    claim.setClaimId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CLAIM_ID)));
                    claim.setItemId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CLAIM_ITEM_ID)));
                    claim.setClaimantId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CLAIMANT_ID)));
                    claim.setOwnerId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_OWNER_ID)));
                    claim.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)));
                    claim.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLAIM_STATUS)));
                    claim.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLAIM_CREATED_AT)));

                    // Populate cached information for UI
                    Item item = getItemById(claim.getItemId());
                    if (item != null) {
                        claim.setItemName(item.getItemName());
                        claim.setItemImageUri(item.getImageUri());
                    }

                    User claimant = getUserById(claim.getClaimantId());
                    if (claimant != null) {
                        claim.setClaimantName(claimant.getNickname());
                    }

                    User owner = getUserById(claim.getOwnerId());
                    if (owner != null) {
                        claim.setOwnerName(owner.getNickname());
                    }

                    claims.add(claim);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return claims;
    }
}
