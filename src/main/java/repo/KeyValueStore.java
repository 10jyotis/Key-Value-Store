package repo;

import model.Entry;
import model.Record;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class KeyValueStore {

    private static KeyValueStore instance = new KeyValueStore();

    private Map<String, Record> dataStore = new ConcurrentHashMap<>();

    private KeyValueStore() {

    }

    public static KeyValueStore getInstance() {
        return instance;
    }

    public void put(String key, Record record) {
        this.dataStore.put(key, record);
    }

    public Optional<Record> get(String key) {
        return Optional.ofNullable(this.dataStore.getOrDefault(key, null));
    }

    public void delete(String key) {
        this.dataStore.remove(key);
    }

    public List<Entry> store() {
        return this.dataStore.entrySet()
                .stream()
                .map(this::buildResponseEntry)
                .collect(Collectors.toList());
    }

    private Entry buildResponseEntry(Map.Entry<String, Record> recordEntry) {
        return new Entry(recordEntry.getKey(), recordEntry.getValue());
    }

}
