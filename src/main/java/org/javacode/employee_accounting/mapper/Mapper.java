package org.javacode.employee_accounting.mapper;

public interface Mapper<From, To> {

    To map(From obj);
    default To map(From fromObject, To toObject) {
        return toObject;
    }

}
