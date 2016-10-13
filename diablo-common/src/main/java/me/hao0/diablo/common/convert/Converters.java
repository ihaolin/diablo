package me.hao0.diablo.common.convert;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.base.Converter;
import com.google.common.primitives.*;
import me.hao0.diablo.common.util.JsonUtil;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class Converters {

    /**
     * Determine the converter with the field type
     * @param field the field
     * @return the Converter
     */
    public static Converter<String, ?> determine(Field field) {

        Class clazz = field.getType();

        // Primitive type
        Converter<String, ?> converter = determinePrimitiveConverter(clazz);
        if (converter != null){
            return converter;
        }

        // List or Set or Map
        if (List.class.equals(clazz) || Map.class.equals(clazz) || Set.class.equals(clazz)){
            return determineCollectionConverter((ParameterizedType)field.getGenericType());
        }

        // Common Json Object
        return new JsonConverter(clazz);
    }

    /**
     * Determine the converter with the object
     * @param genericObj the generic object
     * @return the Converter
     */
    public static Converter<String, ?> determine(Object genericObj) {

        Type type = ((ParameterizedType)genericObj.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

        // Primitive type
        Converter<String, ?> converter = determinePrimitiveConverter(type);
        if (converter != null){
            return converter;
        }

        // List or Set or Map
        if (type instanceof ParameterizedType){
            return determineCollectionConverter((ParameterizedType)type);
        }

        // Common json object
        return new JsonConverter((Class<?>)type);
    }

    /**
     * Determine the primitive type's converter
     * @param type the primitive type
     * @return the primitive type's converter or null
     */
    public static Converter<String, ?> determinePrimitiveConverter(Type type){
        if (String.class.equals(type)){
            return StringConverter.INSTANCE;
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)){
            return BooleanConverter.INSTANCE;
        } else if (Integer.class.equals(type) || int.class.equals(type)){
            return Ints.stringConverter();
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            return Longs.stringConverter();
        } else if (Short.class.equals(type) || short.class.equals(type)){
            return Shorts.stringConverter();
        } else if (Float.class.equals(type) || float.class.equals(type)){
            return Floats.stringConverter();
        } else if (Double.class.equals(type) || double.class.equals(type)){
            return Doubles.stringConverter();
        }
        return null;
    }

    /**
     * Determine the collection type's converter
     * @param ptype the ParameterizedType
     * @return the collection type's converter
     */
    public static JavaTypeConverter determineCollectionConverter(ParameterizedType ptype){
        JavaType javaType;
        Type rawType = ptype.getRawType();
        if (List.class.equals(rawType)){
            // List
            Class<?> listGenericType = (Class<?>)ptype.getActualTypeArguments()[0];
            javaType = JsonUtil.INSTANCE.createCollectionType(List.class, listGenericType);
        } else if (Set.class.equals(rawType)) {
            // Set
            Class<?> setGenericType = (Class<?>)ptype.getActualTypeArguments()[0];
            javaType = JsonUtil.INSTANCE.createCollectionType(Set.class, setGenericType);
        } else {
            // Map
            Class<?> mapKeyType = (Class<?>)ptype.getActualTypeArguments()[0];
            Class<?> mapValueType = (Class<?>)ptype.getActualTypeArguments()[1];
            javaType = JsonUtil.INSTANCE.createCollectionType(Map.class, mapKeyType, mapValueType);
        }
        return new JavaTypeConverter(javaType);
    }
}
