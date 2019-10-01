package com.dangerye.powerful.builder;

import java.util.HashSet;
import java.util.Set;

public class CollectionBuilder {

    public static <T> SetBuilder<T> setBuilder() {
        return setBuilder(new HashSet<>());
    }

    public static <T> SetBuilder<T> setBuilder(Set<T> set) {
        return new SetBuilder<>(set);
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
}
