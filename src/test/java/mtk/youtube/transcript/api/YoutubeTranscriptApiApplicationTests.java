package mtk.youtube.transcript.api;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@DataMongoTest
@Configuration
class YoutubeTranscriptApiApplicationTests {

	@Autowired
	private MongoTemplate mongoTemplate;

	@AfterEach
	void cleanUpDatabase() {
		mongoTemplate.getDb().drop();
	}



	@Test
	void contextLoads() {
		// given
		DBObject objectToSave = BasicDBObjectBuilder.start()
				.add("key", "value")
				.get();

		// when
		mongoTemplate.save(objectToSave, "collection");

		// then
		Assertions.assertThat(mongoTemplate.findAll(DBObject.class, "collection")).extracting("key")
				.containsOnly("value");
	}

}
