// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.etisalat.thirdparty;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventsData extends SQLiteOpenHelper
{

	private static final String DATABASE_NAME = "mobileassistantAPP.db";
    private static final int DATABASE_VERSION = 2;

    public EventsData(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE IF NOT EXISTS USER (ASSISTANT_TYPE TEXT, ASSISTANT_NUMBER TEXT, ASSISTANT_TIME TEXT, ENABLED_DISABLED TEXT)";
		db.execSQL(sql);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS USER";
		db.execSQL(sql);
		onCreate(db);
    }
}
