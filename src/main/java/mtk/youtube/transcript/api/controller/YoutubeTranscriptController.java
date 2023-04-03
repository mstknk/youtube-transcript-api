package mtk.youtube.transcript.api.controller;

import mtk.youtube.transcript.api.document.Transcript;
import mtk.youtube.transcript.api.dto.SearchResult;
import mtk.youtube.transcript.api.model.TranscriptSearch;
import mtk.youtube.transcript.api.service.YoutubeTranscriptService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class YoutubeTranscriptController {

	private YoutubeTranscriptService youtubeTranscriptService;

	public YoutubeTranscriptController(YoutubeTranscriptService youtubeTranscriptService) {
		this.youtubeTranscriptService = youtubeTranscriptService;
	}

	@PostMapping(path = "/transcript", consumes = "application/json", produces = "application/json")
	public SearchResult transcriptSearch(@RequestBody TranscriptSearch transcriptSearch) {

		List<Transcript> transcripts = youtubeTranscriptService.search(transcriptSearch.getWord());
		SearchResult searchResult = new SearchResult();
		//TODO mapp and group by the youtubeid
		searchResult.setTranscripts(transcripts);

		return searchResult;
	}

	@PostMapping(path = "/transcript/{youtubeId}", produces = "application/json")
	public ResponseEntity StoreTranscript(@PathVariable String youtubeId) {
		youtubeTranscriptService.store(youtubeId);
		return ResponseEntity.ok().build();
	}

}
