package mtk.youtube.transcript.api;

import mtk.youtube.transcript.api.document.Transcript;
import mtk.youtube.transcript.api.document.Youtube;
import mtk.youtube.transcript.api.repository.TranscriptMongoRepository;
import mtk.youtube.transcript.api.repository.TranscriptYoutubeRepository;
import mtk.youtube.transcript.api.service.YoutubeTranscriptService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YoutubeTranscriptServiceIntegrationTest {

	public static final String YOUTUBE_ID = "xyz123";

	private YoutubeTranscriptService youtubeTranscriptService;

	@Autowired
	private TranscriptMongoRepository transcriptMongoRepository;

	@Autowired
	private TranscriptYoutubeRepository transcriptYoutubeRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	private RestTemplate restTemplate;
	private RestTemplateBuilder restTemplateBuilder;

	@BeforeEach
	void setupService() {
		restTemplateBuilder = mock(RestTemplateBuilder.class);
		restTemplate = mock(RestTemplate.class);

		when(restTemplateBuilder.build()).thenReturn(restTemplate);

		youtubeTranscriptService = new YoutubeTranscriptService(restTemplateBuilder, transcriptMongoRepository, transcriptYoutubeRepository);
		TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
				.onField("text")
				.build();

		mongoTemplate.indexOps(Transcript.class).ensureIndex(textIndex);

	}

	@Test
	@Order(1)
	public void testStore() {

		ResponseEntity<List<Transcript>> responseEntity = ResponseEntity.ok(getDummyTranscripts());
		when(restTemplate.exchange(anyString(), ArgumentMatchers.eq(HttpMethod.POST), isNull(), any(ParameterizedTypeReference.class)))
				.thenReturn(responseEntity);
		// Store transcripts for a YouTube video
		youtubeTranscriptService.store(YOUTUBE_ID);

		// Assert that the transcripts were stored in both repositories
		List<Transcript> transcripts = transcriptMongoRepository.findAll();
		assertThat(transcripts).hasSizeGreaterThan(0);

		List<Youtube> youtubes = transcriptYoutubeRepository.findAll();
		assertThat(youtubes).hasSize(1);
		assertThat(youtubes.get(0).getYoutubeId()).isEqualTo(YOUTUBE_ID);
		assertThat(youtubes.get(0).getTranscriptList()).hasSizeGreaterThan(0);
	}

	@Test
	@Order(2)
	public void testSearch() {

		// Search for transcripts containing the word "world"
		List<Transcript> results = youtubeTranscriptService.search("world");

		// Assert that the results match our expectations
		assertThat(results).hasSize(2);
		assertThat(results).containsAll(getDummyTranscripts());
	}

	private List<Transcript> getDummyTranscripts() {
		Transcript transcript1 = new Transcript();
		transcript1.setYoutubeId(YOUTUBE_ID);
		transcript1.setStart(0.0);
		transcript1.setDuration(10.0);
		transcript1.setText("Hello world");

		Transcript transcript2 = new Transcript();
		transcript2.setYoutubeId(YOUTUBE_ID);
		transcript2.setStart(10.0);
		transcript2.setDuration(20.0);
		transcript2.setText("Goodbye world");

		return Arrays.asList(transcript1, transcript2);
	}

}
