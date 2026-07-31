package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Identifiable;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class InMemoryStorage<T extends Identifiable> implements BaseStorage<T> {
    protected final Map<Long, T> storage = new HashMap<>();
    private final CopyUtil copyUtil = new CopyUtil();

    @Override
    public T add(T data) {
        data.setId(getNextId());
        storage.put(data.getId(), data);
        return data;
    }

    @Override
    public T get(Long dataId) {
        return getOrElseThrow(dataId);
    }

    @Override
    public List<T> findAll() {
        return storage.values().stream().toList();
    }

    @Override
    public T delete(Long dataId) {
        return storage.remove(dataId);
    }

    @Override
    public T update(T updatedData) {
        T dataToUpdate = getOrElseThrow(updatedData.getId());
        copyUtil.copyNonNullProperties(updatedData, dataToUpdate);
        return dataToUpdate;
    }

    protected long getNextId() {
        long currentMaxId = storage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    protected T getOrElseThrow(Long id) {
        String name = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].toString();
        return Optional.ofNullable(storage.get(id))
                .orElseThrow(() -> new NotFoundException(name + " with id=" + id + " не найден", id.toString()));
    }
}
