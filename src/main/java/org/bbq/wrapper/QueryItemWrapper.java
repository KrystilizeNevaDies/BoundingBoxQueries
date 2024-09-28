package org.bbq.wrapper;

import org.bbq.QueryItem;

public interface QueryItemWrapper<T> {
    QueryItem toQueryItem(T t);
    T fromQueryItem(QueryItem queryItem);
}
