package top.mrexgo.demobpm.common.config;

import cn.hutool.core.lang.Snowflake;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import top.mrexgo.demobpm.common.annotation.IncKey;
import top.mrexgo.demobpm.common.exception.ServiceException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author liangjuhong
 * @since 2021-06-17
 */
@Component
@RequiredArgsConstructor
public class MongoSaveEventListener extends AbstractMongoEventListener<Object> {

    private final Snowflake snowflake;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            if (field.isAnnotationPresent(IncKey.class)) {
                if (!Long.class.equals(field.getType())) {
                    throw new ServiceException("id参数类型错");
                }
                Long val = null;
                try {
                    String name = field.getName();
                    Method m = source.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
                    val = (Long) m.invoke(source);
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (ObjectUtils.isNotEmpty(val)) {
                    return;
                }

                field.set(source, snowflake.nextId());
            }
        });

    }
}
