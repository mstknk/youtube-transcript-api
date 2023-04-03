package mtk.youtube.transcript.api.dto;

import mtk.youtube.transcript.api.document.Transcript;

import java.util.List;

public class SearchResult {
	private List<Transcript> transcripts;

	public List<Transcript> getTranscripts() {
		return transcripts;
	}

	public void setTranscripts(List<Transcript> transcripts) {
		this.transcripts = transcripts;
	}
}
