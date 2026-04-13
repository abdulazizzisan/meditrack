package dev.zisan.meditrack.appointment.controller;

import dev.zisan.meditrack.appointment.dto.AppointmentResponse;
import dev.zisan.meditrack.appointment.dto.CreateAppointmentRequest;
import dev.zisan.meditrack.appointment.dto.UpdateAppointmentStatusRequest;
import dev.zisan.meditrack.appointment.service.AppointmentService;
import dev.zisan.meditrack.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@PostMapping("/appointments")
	@PreAuthorize("hasAuthority('PATIENT') or hasAuthority('ADMIN')")
	public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
			@Valid @RequestBody CreateAppointmentRequest request) {
		AppointmentResponse appointment = appointmentService.bookAppointment(request);

		ApiResponse<AppointmentResponse> response = ApiResponse.<AppointmentResponse>builder()
			.statusCode(HttpStatus.CREATED.value())
			.message("Appointment booked successfully.")
			.data(appointment)
			.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/appointments/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or @appointmentSecurity.canAccess(authentication, #id)")
	public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointment(@PathVariable Long id) {
		AppointmentResponse appointment = appointmentService.getAppointmentById(id);

		ApiResponse<AppointmentResponse> response = ApiResponse.<AppointmentResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Appointment retrieved successfully.")
			.data(appointment)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/doctors/{doctorId}/appointments")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @doctorSecurity.isOwner(authentication, #doctorId))")
	public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> getDoctorAppointments(
			@PathVariable Long doctorId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<AppointmentResponse> appointments = appointmentService.getDoctorAppointments(doctorId, pageable);

		ApiResponse<Page<AppointmentResponse>> response = ApiResponse.<Page<AppointmentResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctor appointments retrieved successfully.")
			.data(appointments)
			.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping("/appointments/{id}/status")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @appointmentSecurity.isDoctorOwner(authentication, #id))")
	public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
			@PathVariable Long id,
			@Valid @RequestBody UpdateAppointmentStatusRequest request) {
		AppointmentResponse appointment = appointmentService.updateAppointmentStatus(id, request);

		ApiResponse<AppointmentResponse> response = ApiResponse.<AppointmentResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Appointment status updated successfully.")
			.data(appointment)
			.build();

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/appointments/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('PATIENT') and @appointmentSecurity.isPatientOwner(authentication, #id))")
	public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(@PathVariable Long id) {
		AppointmentResponse appointment = appointmentService.cancelAppointment(id);

		ApiResponse<AppointmentResponse> response = ApiResponse.<AppointmentResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Appointment cancelled successfully.")
			.data(appointment)
			.build();

		return ResponseEntity.ok(response);
	}
}
