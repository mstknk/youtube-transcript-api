package mtk.youtube.transcript.api.service;

import mtk.youtube.transcript.api.document.Transcript;
import mtk.youtube.transcript.api.document.Youtube;
import mtk.youtube.transcript.api.repository.TranscriptMongoRepository;
import mtk.youtube.transcript.api.repository.TranscriptYoutubeRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class YoutubeTranscriptService {

	private TranscriptMongoRepository transcriptMongoRepository;

	private TranscriptYoutubeRepository transcriptYoutubeRepository;

	@Value("${transcripts.api.url}")
	private String transcriptsApiUrl;

	private final RestTemplate restTemplate;

	public YoutubeTranscriptService(RestTemplateBuilder restTemplateBuilder, TranscriptMongoRepository transcriptMongoRepository,
			TranscriptYoutubeRepository transcriptYoutubeRepository) {

		this.restTemplate = restTemplateBuilder.build();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
		messageConverters.add(converter);
		this.restTemplate.setMessageConverters(messageConverters);
		this.transcriptMongoRepository = transcriptMongoRepository;
		this.transcriptYoutubeRepository = transcriptYoutubeRepository;
	}

	public List<Transcript> search(String word) {
		return transcriptMongoRepository.findAllBy(new TextCriteria().matchingAny(word), Sort.by("start"));
	}

	public void store(String youtubeId) {
		ResponseEntity<List<Transcript>> responseEntity = restTemplate.exchange(transcriptsApiUrl + youtubeId, HttpMethod.POST, null,
				new ParameterizedTypeReference<>() {
				});

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			List<Transcript> transcripts = responseEntity.getBody();
			Youtube youtube = new Youtube();
			youtube.setYoutubeId(youtubeId);
			youtube.setTranscriptList(transcripts);

			transcriptYoutubeRepository.insert(youtube);
			// TODO check this logic later we might change mongo to elastic search
			transcripts.forEach(e -> e.setYoutubeId(youtubeId));
			transcriptMongoRepository.saveAll(transcripts);
		}

	}
}
