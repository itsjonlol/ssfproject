package sg.edu.nus.iss.ssfproject.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.ssfproject.constant.ConstantVar;

@Repository
public class UserRepo {
    
    @Autowired
    @Qualifier(ConstantVar.template01)
    private RedisTemplate<String, String> template;

    public void setHash(String redisKey, String mapKey, String value) {
        template.opsForHash().put(redisKey,mapKey, value);
    }

    public Boolean hashExists(String redisKey) {
        return template.hasKey(redisKey);
    }
    public String getValueFromHash(String redisKey, String mapKey) {
        return (String) template.opsForHash().get(redisKey, mapKey); //hget c01 email
        //hgetall c01
    }
    public Boolean hasKey(String redisKey, String mapKey) { //hexists c01 email
        return template.opsForHash().hasKey(redisKey, mapKey);
    }
}
