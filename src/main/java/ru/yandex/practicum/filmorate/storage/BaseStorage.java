package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Identifiable;

import java.util.List;
import java.util.Optional;

public interface BaseStorage<T extends Identifiable> {
    T add(T data);

    Optional<T> get(Long dataId);

    List<T> findAll();

    T delete(Long dataId);

    T update(T updatedData);
}
