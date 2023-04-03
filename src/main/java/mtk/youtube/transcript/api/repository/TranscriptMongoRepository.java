package mtk.youtube.transcript.api.repository;

import mtk.youtube.transcript.api.document.Transcript;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TranscriptMongoRepository extends MongoRepository<Transcript, String> {

	List<Transcript> findAllBy(TextCriteria criteria, Sort sort);


}
