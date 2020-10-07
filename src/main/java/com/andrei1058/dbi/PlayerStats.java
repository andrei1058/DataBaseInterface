package com.andrei1058.dbi;

import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.type.UUIDColumn;
import com.andrei1058.dbi.column.type.IntegerColumn;

import java.util.UUID;

public class PlayerStats {

    public static Column<UUID> PLAYER_UUID = new UUIDColumn("uuid", UUID.randomUUID());
    public static Column<Integer> PLAYER_KILLS = new IntegerColumn("kills", 0, 6);
}
