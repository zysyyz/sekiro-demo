package com.virjar.sekiro.weishi;

import com.virjar.sekiro.api.SekiroResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
    public static Map<Object, SekiroResponse> requestTaskMap = new ConcurrentHashMap<>();
}
