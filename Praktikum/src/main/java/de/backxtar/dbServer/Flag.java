package de.backxtar.dbServer;

import com.google.gson.annotations.SerializedName;

public enum Flag {
    @SerializedName("AVAILABLE") AVAILABLE,
    @SerializedName("UPDATE") UPDATE,
    @SerializedName("DELETE") DELETE
}
