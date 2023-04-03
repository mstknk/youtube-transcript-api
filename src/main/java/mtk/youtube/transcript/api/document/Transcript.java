package mtk.youtube.transcript.api.document;

import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transcript")
public class Transcript {

	@TextIndexed
	private String text;
	private String youtubeId;
	private Double start;
	private Double duration;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getYoutubeId() {
		return youtubeId;
	}

	public void setYoutubeId(String youtubeId) {
		this.youtubeId = youtubeId;
	}

	public Double getStart() {
		return start;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Transcript that = (Transcript) o;

		if (text != null ? !text.equals(that.text) : that.text != null)
			return false;
		if (youtubeId != null ? !youtubeId.equals(that.youtubeId) : that.youtubeId != null)
			return false;
		if (start != null ? !start.equals(that.start) : that.start != null)
			return false;
		return duration != null ? duration.equals(that.duration) : that.duration == null;
	}

}
