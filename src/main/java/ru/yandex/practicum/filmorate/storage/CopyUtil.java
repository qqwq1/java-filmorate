package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public final class CopyUtil {

    public static void copyNonNullProperties(Object source, Object target) {
        if (source == null) {
            throw new ValidationException("Ошибка, фильм для обновления не может быть равен null", target);
        }
        String[] nullFieldsNames = getNullFieldsNames(source);
        BeanUtils.copyProperties(source, target, nullFieldsNames);
    }

    private static String[] getNullFieldsNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() != null) {
                Object srcValue = src.getPropertyValue(pd.getName());

                if (srcValue == null) {
                    emptyNames.add(pd.getName());
                }
            }
        }

        return emptyNames.toArray(new String[0]);
    }
}
