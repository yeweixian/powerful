package com.dangerye.powerful.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionBuilder {

    public static <T> SetBuilder<T> setBuilder() {
        return setBuilder(new HashSet<>());
    }

    public static <T> SetBuilder<T> setBuilder(Set<T> set) {
        return new SetBuilder<>(set);
    }

    public static <T> ListBuilder<T> listBuilder() {
        return listBuilder(new ArrayList<>());
    }

    public static <T> ListBuilder<T> listBuilder(List<T> list) {
        return new ListBuilder<>(list);
    }

    public static <K, V> MapBuilder<K, V> mapBuilder() {
        return mapBuilder(new HashMap<>());
    }

    public static <K, V> MapBuilder<K, V> mapBuilder(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    public static class SetBuilder<T> {
        private Set<T> set;

        private SetBuilder(Set<T> set) {
            this.set = set;
        }

        public SetBuilder<T> add(T t) {
            this.set.add(t);
            return this;
        }

        public Set<T> build() {
            return this.set;
        }
    }

    public static class ListBuilder<T> {
        private List<T> list;

        private ListBuilder(List<T> list) {
            this.list = list;
        }

        public ListBuilder<T> add(T t) {
            this.list.add(t);
            return this;
        }

        public List<T> build() {
            return this.list;
        }
    }

    public static class MapBuilder<K, V> {
        private Map<K, V> map;

        private MapBuilder(Map<K, V> map) {
            this.map = map;
        }

        public MapBuilder<K, V> put(K key, V value) {
            this.map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return this.map;
        }
    }
}
