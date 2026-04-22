package com.ngulik.kotakpos_admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductUnit {

    //          SATUAN DASAR
    PCS("PCS", "Pieces"),
    UNIT("UNIT", "Unit"),
    ITEM("ITEM", "Item"),

    //          SATUAN BERAT
    KG("KG","Kilogram"),
    GR("GR","GRAM"),
    ONS("ONS","Ons"),
    MG("MG","Miligram"),

//              SATUAN VOLUME
    LTR("LTR","liter"),
    ML("ML", "Mililiter"),
    CUP("CUP", "Cup"),
    GLASS("GLS", "Gelas");

    private final String code;
    private final String name;
}
