package org.bibliome.util.mappers;

public final class ToStringMapper<T> implements Mapper<T,String> {
    @Override
    public String map(T x) {
        return x.toString();
    }
}