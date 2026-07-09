package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Identifiable;

import java.util.List;

public interface BaseStorage<T extends Identifiable> {
    T add(T data);

    T get(Long dataId);

    List<T> findAll();

    T delete(Long dataId);

    T update(T updatedData);
}
