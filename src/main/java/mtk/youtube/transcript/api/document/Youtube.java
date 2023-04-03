package mtk.youtube.transcript.api.document;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "youtube")
public class Youtube {
	private String youtubeId;
	private List<Transcript> transcriptList;

	public String getYoutubeId() {
		return youtubeId;
	}

	public void setYoutubeId(String youtubeId) {
		this.youtubeId = youtubeId;
	}

	public List<Transcript> getTranscriptList() {
		return transcriptList;
	}

	public void setTranscriptList(List<Transcript> transcriptList) {
		this.transcriptList = transcriptList;
	}
}
