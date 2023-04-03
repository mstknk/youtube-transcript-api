package mtk.youtube.transcript.api.controller;

import mtk.youtube.transcript.api.document.Transcript;
import mtk.youtube.transcript.api.dto.SearchResult;
import mtk.youtube.transcript.api.model.TranscriptSearch;
import mtk.youtube.transcript.api.service.YoutubeTranscriptService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class YoutubeTranscriptControllerTest {

	private MockMvc mockMvc;

	@Mock
	private YoutubeTranscriptService youtubeTranscriptService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new YoutubeTranscriptController(youtubeTranscriptService)).build();
	}

	@Test
	public void testTranscriptSearch() throws Exception {
		// Set up mock service response
		List<Transcript> transcripts = new ArrayList<>();
		Transcript transcript1 = new Transcript();
		transcript1.setYoutubeId("video1");
		transcript1.setText("hello world");
		transcript1.setStart(1.0);
		transcript1.setDuration(5.0);
		Transcript transcript2 = new Transcript();
		transcript2.setYoutubeId("video2");
		transcript2.setText("world hello");
		transcript2.setStart(1.0);
		transcript2.setDuration(5.0);
		transcripts.add(transcript1);
		transcripts.add(transcript2);
		when(youtubeTranscriptService.search(anyString())).thenReturn(transcripts);

		// Set up request body
		TranscriptSearch transcriptSearch = new TranscriptSearch();
		transcriptSearch.setWord("hello");

		// Perform request
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transcript")
				.content("{ \"word\": \"hello\" }")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Verify response body
		String responseJson = result.getResponse().getContentAsString();
		SearchResult searchResult = new SearchResult();
		searchResult.setTranscripts(transcripts);
		String expectedJson = "{\"transcripts\":[{\"text\":\"hello world\",\"youtubeId\":\"video1\",\"start\":1.0,\"duration\":5.0},{\"text\":\"world hello\",\"youtubeId\":\"video2\",\"start\":1.0,\"duration\":5.0}]}";
		assertEquals(expectedJson, responseJson);
	}

	@Test
	public void testStoreTranscript() throws Exception {
		// Perform request
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transcript/video1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		// Verify response body
		assertEquals("", result.getResponse().getContentAsString());
	}

}
