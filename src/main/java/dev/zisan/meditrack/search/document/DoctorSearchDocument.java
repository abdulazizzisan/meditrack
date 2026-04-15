package dev.zisan.meditrack.search.document;

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
@Document(indexName = "doctor_search")
public class DoctorSearchDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Long)
	private Long userId;

	@Field(type = FieldType.Text)
	private String fullName;

	@Field(type = FieldType.Text)
	private String specialization;

	@Field(type = FieldType.Text)
	private String hospitalAffiliation;

	@Field(type = FieldType.Keyword)
	private String licenseNumber;

	@Field(type = FieldType.Boolean)
	private boolean enabled;
}
