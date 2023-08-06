package com.midtern.SpringCommerce.converter;

import com.midtern.SpringCommerce.dto.response.UserResponse;
import com.midtern.SpringCommerce.entity.User;

import java.util.List;

public class UserConverter {
    public static UserResponse toResponse(User user, String[] attrs) {
        var attributes = List.of(attrs);
        var fields = UserResponse.class.getDeclaredFields();

        var builder = new UserResponse();
        for (var field: fields) {
            if (attributes.contains(field.getName())) {
                try {
                    var method = User.class.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
                    var value = method.invoke(user);
                    var builderMethod = builder.getClass().getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
                    builderMethod.invoke(builder, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return builder;
    }
}
