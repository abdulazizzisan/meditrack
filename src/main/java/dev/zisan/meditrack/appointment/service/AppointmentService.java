package dev.zisan.meditrack.appointment.service;

import dev.zisan.meditrack.appointment.dto.AppointmentResponse;
import dev.zisan.meditrack.appointment.dto.CreateAppointmentRequest;
import dev.zisan.meditrack.appointment.dto.UpdateAppointmentStatusRequest;
import dev.zisan.meditrack.appointment.entity.Appointment;
import dev.zisan.meditrack.appointment.entity.AppointmentStatus;
import dev.zisan.meditrack.appointment.repository.AppointmentRepository;
import dev.zisan.meditrack.common.exception.BadRequestException;
import dev.zisan.meditrack.common.exception.ConflictException;
import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.doctor.entity.Doctor;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import dev.zisan.meditrack.patient.entity.Patient;
import dev.zisan.meditrack.patient.repository.PatientRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

	private static final List<AppointmentStatus> ACTIVE_STATUSES = List.of(
		AppointmentStatus.PENDING,
		AppointmentStatus.CONFIRMED
	);

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;

	public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
			DoctorRepository doctorRepository) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
	}

	@Transactional
	public AppointmentResponse bookAppointment(CreateAppointmentRequest request) {
		Patient patient = patientRepository.findWithUserById(request.patientId())
			.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.patientId()));

		Doctor doctor = doctorRepository.findWithUserById(request.doctorId())
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.doctorId()));

		boolean slotTaken = appointmentRepository.existsByDoctorIdAndScheduledAtAndStatusIn(
			request.doctorId(),
			request.scheduledAt(),
			ACTIVE_STATUSES
		);

		if (slotTaken) {
			throw new ConflictException("The doctor already has an appointment scheduled for this time.");
		}

		Appointment appointment = Appointment.builder()
			.patient(patient)
			.doctor(doctor)
			.scheduledAt(request.scheduledAt())
			.status(AppointmentStatus.PENDING)
			.notes(request.notes())
			.build();

		Appointment savedAppointment = appointmentRepository.save(appointment);
		return mapToResponse(loadAppointment(savedAppointment.getId()));
	}

	@Transactional(readOnly = true)
	public AppointmentResponse getAppointmentById(Long appointmentId) {
		return mapToResponse(loadAppointment(appointmentId));
	}

	@Transactional(readOnly = true)
	public Page<AppointmentResponse> getDoctorAppointments(Long doctorId, Pageable pageable) {
		doctorRepository.findWithUserById(doctorId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

		return appointmentRepository.findByDoctorIdOrderByScheduledAtDesc(doctorId, pageable)
			.map(this::mapToResponse);
	}

	@Transactional
	public AppointmentResponse updateAppointmentStatus(Long appointmentId, UpdateAppointmentStatusRequest request) {
		Appointment appointment = loadAppointment(appointmentId);

		if (request.status() == AppointmentStatus.PENDING) {
			throw new BadRequestException("Appointment status cannot be changed back to PENDING.");
		}

		if (appointment.getStatus() == AppointmentStatus.CANCELLED
				|| appointment.getStatus() == AppointmentStatus.COMPLETED) {
			throw new BadRequestException("Completed or cancelled appointments cannot be modified.");
		}

		appointment.setStatus(request.status());
		return mapToResponse(appointment);
	}

	@Transactional
	public AppointmentResponse cancelAppointment(Long appointmentId) {
		Appointment appointment = loadAppointment(appointmentId);

		if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
			throw new BadRequestException("Completed appointments cannot be cancelled.");
		}

		if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
			throw new BadRequestException("Appointment is already cancelled.");
		}

		appointment.setStatus(AppointmentStatus.CANCELLED);
		return mapToResponse(appointment);
	}

	private Appointment loadAppointment(Long appointmentId) {
		return appointmentRepository.findDetailsById(appointmentId)
			.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
	}

	private AppointmentResponse mapToResponse(Appointment appointment) {
		return AppointmentResponse.builder()
			.id(appointment.getId())
			.patientId(appointment.getPatient().getId())
			.patientName(appointment.getPatient().getUser().getFullName())
			.doctorId(appointment.getDoctor().getId())
			.doctorName(appointment.getDoctor().getUser().getFullName())
			.doctorSpecialization(appointment.getDoctor().getSpecialization())
			.scheduledAt(appointment.getScheduledAt())
			.status(appointment.getStatus())
			.notes(appointment.getNotes())
			.createdAt(appointment.getCreatedAt())
			.updatedAt(appointment.getUpdatedAt())
			.build();
	}
}
