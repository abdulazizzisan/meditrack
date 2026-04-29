package dev.zisan.meditrack.search.document;

import java.util.List;
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
@Document(indexName = "patient_search")
public class PatientSearchDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Long)
	private Long userId;

	@Field(type = FieldType.Text)
	private String fullName;

	@Field(type = FieldType.Keyword)
	private String gender;

	@Field(type = FieldType.Keyword)
	private String bloodType;

	@Field(type = FieldType.Keyword)
	private String phone;

	@Field(type = FieldType.Text)
	private List<String> diagnoses;

	@Field(type = FieldType.Text)
	private List<String> medicalNotes;

	@Field(type = FieldType.Text)
	private List<String> medications;

	@Field(type = FieldType.Boolean)
	private boolean enabled;
}
