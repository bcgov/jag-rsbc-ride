package bcgov.rsbc.ride.kafka;

import java.time.LocalDate;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;

@MongoEntity(collection = "ride_producerapi_auth_app_apikey")
public class apiKeys extends PanacheMongoEntity{

    @BsonProperty("appid_id")
    public String appid_id;

    @BsonProperty("apikeyval")
    public String apikeyval;

}
