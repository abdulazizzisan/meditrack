package dev.zisan.meditrack.search.document;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "medication_search")
public class MedicationSearchDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Long)
	private Long patientId;

	@Field(type = FieldType.Long)
	private Long doctorId;

	@Field(type = FieldType.Text)
	private String patientName;

	@Field(type = FieldType.Text)
	private String doctorName;

	@Field(type = FieldType.Text)
	private String name;

	@Field(type = FieldType.Text)
	private String dosage;

	@Field(type = FieldType.Text)
	private String frequency;

	@Field(type = FieldType.Date)
	private LocalDate startDate;

	@Field(type = FieldType.Date)
	private LocalDate endDate;

	@Field(type = FieldType.Boolean)
	private boolean active;

	@Field(type = FieldType.Boolean)
	private boolean visible;
}
