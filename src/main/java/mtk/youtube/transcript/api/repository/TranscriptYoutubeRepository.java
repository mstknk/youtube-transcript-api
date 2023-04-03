package mtk.youtube.transcript.api.repository;

import mtk.youtube.transcript.api.document.Youtube;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TranscriptYoutubeRepository extends MongoRepository<Youtube, String> {


}
