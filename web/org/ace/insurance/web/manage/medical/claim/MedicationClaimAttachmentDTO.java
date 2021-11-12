package org.ace.insurance.web.manage.medical.claim;

import org.ace.insurance.web.common.CommonDTO;

public class MedicationClaimAttachmentDTO extends CommonDTO {

	private int version;
	private String id;
	private String name;
	private String filePath;
	private MedicationClaimDTO medicationClaim;
	private boolean exitEntity;

	public MedicationClaimAttachmentDTO(String name, String filePath) {
		this.name = name;
		this.filePath = filePath;
	}

	public int getVersion() {
		return version;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFilePath() {
		return filePath;
	}

	public MedicationClaimDTO getMedicationClaim() {
		return medicationClaim;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setMedicationClaim(MedicationClaimDTO medicationClaim) {
		this.medicationClaim = medicationClaim;
	}

	public MedicationClaimAttachmentDTO() {
	}

	public boolean isExitEntity() {
		return exitEntity;
	}

	public void setExitEntity(boolean exitEntity) {
		this.exitEntity = exitEntity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (exitEntity ? 1231 : 1237);
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((medicationClaim == null) ? 0 : medicationClaim.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MedicationClaimAttachmentDTO other = (MedicationClaimAttachmentDTO) obj;
		if (exitEntity != other.exitEntity)
			return false;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (medicationClaim == null) {
			if (other.medicationClaim != null)
				return false;
		} else if (!medicationClaim.equals(other.medicationClaim))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
