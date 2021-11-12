package org.ace.java.component.persistence.interfaces;

import java.util.List;

import org.ace.java.component.persistence.exception.DAOException;

public interface IDataRepository<T> {
	public void insert(Object object) throws DAOException;

	public T update(T param) throws DAOException;

	public void delete(Object object) throws DAOException;

	public void deleteById(Class<T> paramClass, String id) throws DAOException;

	public T findById(Class<T> paramClass, Object paramObject) throws DAOException;

	public List<T> findAll(Class<T> paramClass) throws DAOException;

}
