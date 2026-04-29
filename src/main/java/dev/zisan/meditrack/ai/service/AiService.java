package dev.zisan.meditrack.ai.service;

import dev.zisan.meditrack.ai.dto.NoteSummaryResponse;
import dev.zisan.meditrack.ai.dto.SummarizeNotesRequest;
import dev.zisan.meditrack.ai.dto.TriageRequest;
import dev.zisan.meditrack.ai.dto.TriageResponse;
import dev.zisan.meditrack.common.aop.Loggable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiService {

	private static final String AI_UNAVAILABLE_MESSAGE = "AI service is currently unavailable. Please try again later.";

	private final ChatClient chatClient;

	public AiService(ObjectProvider<ChatClient.Builder> chatClientBuilderProvider) {
		ChatClient.Builder chatClientBuilder = chatClientBuilderProvider.getIfAvailable();
		this.chatClient = chatClientBuilder != null ? chatClientBuilder.build() : null;
	}

	@Loggable
	public TriageResponse triage(TriageRequest request) {
		if (chatClient == null) {
			return triageFallback();
		}

		try {
			TriageResponse response = chatClient.prompt()
				.system("You are a medical triage assistant. Return concise JSON only.")
				.user(buildTriagePrompt(request))
				.call()
				.entity(TriageResponse.class);

			return normalizeTriageResponse(response);
		} catch (Exception exception) {
			log.warn("AI triage request failed: {}", exception.getMessage());
			return triageFallback();
		}
	}

	@Loggable
	public NoteSummaryResponse summarizeNotes(SummarizeNotesRequest request) {
		if (chatClient == null) {
			return noteSummaryFallback();
		}

		try {
			NoteSummaryResponse response = chatClient.prompt()
				.system("You summarize clinical notes. Return concise JSON only.")
				.user(buildSummarizePrompt(request))
				.call()
				.entity(NoteSummaryResponse.class);

			return normalizeNoteSummaryResponse(response);
		} catch (Exception exception) {
			log.warn("AI note summarization request failed: {}", exception.getMessage());
			return noteSummaryFallback();
		}
	}

	private String buildTriagePrompt(TriageRequest request) {
		return """
			Return a JSON object with exactly these fields:
			- urgencyLevel: one of LOW, MEDIUM, HIGH, EMERGENCY
			- possibleConditions: array of 3 short strings
			- suggestedSpecialization: short string
			- fallback: false
			- message: short summary for the user

			Symptoms: %s
			""".formatted(String.join(", ", request.symptoms()));
	}

	private String buildSummarizePrompt(SummarizeNotesRequest request) {
		return """
			Return a JSON object with exactly these fields:
			- chiefComplaint: short string
			- assessment: short string
			- plan: short string
			- fallback: false
			- message: short summary line

			Clinical notes:
			%s
			""".formatted(request.notes());
	}

	private TriageResponse normalizeTriageResponse(TriageResponse response) {
		if (response == null) {
			return triageFallback();
		}

		return TriageResponse.builder()
			.urgencyLevel(defaultText(response.getUrgencyLevel(), "UNKNOWN"))
			.possibleConditions(response.getPossibleConditions() == null ? List.of() : response.getPossibleConditions())
			.suggestedSpecialization(defaultText(response.getSuggestedSpecialization(), "General Practice"))
			.fallback(false)
			.message(defaultText(response.getMessage(), "AI triage completed."))
			.build();
	}

	private NoteSummaryResponse normalizeNoteSummaryResponse(NoteSummaryResponse response) {
		if (response == null) {
			return noteSummaryFallback();
		}

		return NoteSummaryResponse.builder()
			.chiefComplaint(defaultText(response.getChiefComplaint(), "Not available"))
			.assessment(defaultText(response.getAssessment(), "Not available"))
			.plan(defaultText(response.getPlan(), "Not available"))
			.fallback(false)
			.message(defaultText(response.getMessage(), "AI note summary completed."))
			.build();
	}

	private TriageResponse triageFallback() {
		return TriageResponse.builder()
			.urgencyLevel("UNKNOWN")
			.possibleConditions(List.of())
			.suggestedSpecialization("General Practice")
			.fallback(true)
			.message(AI_UNAVAILABLE_MESSAGE)
			.build();
	}

	private NoteSummaryResponse noteSummaryFallback() {
		return NoteSummaryResponse.builder()
			.chiefComplaint("Not available")
			.assessment("Not available")
			.plan("Not available")
			.fallback(true)
			.message(AI_UNAVAILABLE_MESSAGE)
			.build();
	}

	private String defaultText(String value, String fallbackValue) {
		if (value == null || value.isBlank()) {
			return fallbackValue;
		}

		return value.trim();
	}
}
