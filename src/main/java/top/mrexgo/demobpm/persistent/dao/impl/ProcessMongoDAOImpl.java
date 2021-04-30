package top.mrexgo.demobpm.persistent.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import top.mrexgo.demobpm.persistent.dao.ProcessMongoDAO;

/**
 * @author liangjuhong
 * @since 2021/4/30 - 16:21
 */
@Component
@RequiredArgsConstructor
public class ProcessMongoDAOImpl implements ProcessMongoDAO {

    private final MongoTemplate template;
}
