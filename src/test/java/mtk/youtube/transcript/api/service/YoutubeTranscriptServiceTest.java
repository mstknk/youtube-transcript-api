package mtk.youtube.transcript.api.service;

import mtk.youtube.transcript.api.document.Transcript;
import mtk.youtube.transcript.api.document.Youtube;
import mtk.youtube.transcript.api.repository.TranscriptMongoRepository;
import mtk.youtube.transcript.api.repository.TranscriptYoutubeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YoutubeTranscriptServiceTest {

	public static final String YOUTUBE_ID = "YOUTUBE_ID";
	public static final String TEXT = "ziemlich gut hingekriegt innerhalb";
	public static final String SEARCH_TEXT = "ziemlich";
	public static final double DURATION = 2.2;
	public static final double START = 0.0;

	private YoutubeTranscriptService youtubeTranscriptService;

	private TranscriptMongoRepository transcriptMongoRepository;

	private TranscriptYoutubeRepository transcriptYoutubeRepository;

	private RestTemplate restTemplate;
	private RestTemplateBuilder restTemplateBuilder;

	@BeforeEach
	void setupService() {
		restTemplateBuilder = mock(RestTemplateBuilder.class);
		restTemplate = mock(RestTemplate.class);
		transcriptMongoRepository = mock(TranscriptMongoRepository.class);
		transcriptYoutubeRepository = mock(TranscriptYoutubeRepository.class);
		when(restTemplateBuilder.build()).thenReturn(restTemplate);
		youtubeTranscriptService = new YoutubeTranscriptService(restTemplateBuilder, transcriptMongoRepository, transcriptYoutubeRepository);
	}

	@Test
	void searchShouldReturnResults() {
		List<Transcript> searchResulst = getDummySearchResults();
		when(transcriptMongoRepository.findAllBy(any(), any())).thenReturn(getDummySearchResults());

		List<Transcript> searchResponse = youtubeTranscriptService.search(SEARCH_TEXT);
		verify(transcriptMongoRepository, times(1)).findAllBy(any(TextCriteria.class), any(Sort.class));
		Transcript transcript = searchResponse.get(0);
		assertAll("transcript",
				() -> assertEquals(DURATION, transcript.getDuration()),
				() -> assertEquals(START, transcript.getStart()),
				() -> assertEquals("YOUTUBE_ID", transcript.getYoutubeId()),
				() -> assertEquals(TEXT, transcript.getText())
		);

	}

	@Test
	void testStore() {
		List<Transcript> transcripts = new ArrayList<>();
		Transcript transcript1 = new Transcript();
		transcript1.setText("This is transcript 1");
		Transcript transcript2 = new Transcript();
		transcript2.setText("This is transcript 2");
		transcripts.add(transcript1);
		transcripts.add(transcript2);

		ResponseEntity<List<Transcript>> responseEntity = new ResponseEntity<>(transcripts, HttpStatus.OK);
		when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

		youtubeTranscriptService.store(YOUTUBE_ID);

		verify(transcriptYoutubeRepository, times(1)).insert(any(Youtube.class));
		verify(transcriptMongoRepository, times(1)).saveAll(any(List.class));
	}

	private List<Transcript> getDummySearchResults() {
		Transcript transcript1 = new Transcript();
		transcript1.setYoutubeId(YOUTUBE_ID);
		transcript1.setDuration(DURATION);
		transcript1.setStart(START);
		transcript1.setText(TEXT);
		return List.of(transcript1);
	}

}