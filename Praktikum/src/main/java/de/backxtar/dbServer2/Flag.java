package de.backxtar.dbServer2;

import com.google.gson.annotations.SerializedName;

public enum Flag {
    @SerializedName("AVAILABLE") AVAILABLE,
    @SerializedName("UPDATE") UPDATE,
    @SerializedName("DELETE") DELETE
}
