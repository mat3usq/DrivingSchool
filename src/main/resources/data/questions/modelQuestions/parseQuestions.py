import pandas as pd
from transformers import BertTokenizer, BertForSequenceClassification, Trainer, TrainingArguments
import torch
from sklearn.model_selection import train_test_split

# Wczytaj dane treningowe i pytania do kategoryzacji
training_data = pd.read_excel('training_questions.xlsx')
new_questions = pd.read_excel('questions.xlsx')

# Przygotowanie tokenizer i modeli BERT
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')

# Model do klasyfikacji dziedzin
model_dziedziny = BertForSequenceClassification.from_pretrained('bert-base-uncased', num_labels=16, hidden_dropout_prob=0.3, attention_probs_dropout_prob=0.3)

# Model do klasyfikacji specjalistyczności
model_specjalistyczne = BertForSequenceClassification.from_pretrained('bert-base-uncased', num_labels=2, hidden_dropout_prob=0.3, attention_probs_dropout_prob=0.3)

# Zamiana etykiet na format numeryczny
dziedziny_mapping = {
    "Znaki Ostrzegawcze": 0,
    "Znaki nakazu i zakazu": 1,
    "Znaki informacyjne, kierunku": 2,
    "Znaki poziome": 3,
    "Sygnalizacja świetlna": 4,
    "Pierwszeństwo przejazdu": 5,
    "Zatrzymanie, postój, włączenie się do ruchu": 6,
    "Zmiana kierunku jazdy, przejazdy kolejowe": 7,
    "Wyprzedzanie, omijanie, wymijanie cofanie": 8,
    "Szczególna ostrożność": 9,
    "Warunki drogowe": 10,
    "Prędkość, hamowanie, odstępy": 11,
    "Technika jazdy": 12,
    "Obowiązki kierującego, dokumenty": 13,
    "Awaria pojazdu, pierwsza pomoc": 14,
    "Inne": 15
}
training_data['label_dziedzina'] = training_data['Dziedzina'].map(dziedziny_mapping)
training_data['label_specjalistyczne'] = training_data['Specjalistyczne'].apply(lambda x: 1 if x.strip().lower() == 'tak' else 0)

# Podział danych treningowych na zestawy treningowe i walidacyjne
train_texts, val_texts, train_labels_dziedzina, val_labels_dziedzina = train_test_split(
    training_data['Pytanie'].tolist(),
    training_data['label_dziedzina'].tolist(),
    test_size=0.2
)

train_texts_spec, val_texts_spec, train_labels_spec, val_labels_spec = train_test_split(
    training_data['Pytanie'].tolist(),
    training_data['label_specjalistyczne'].tolist(),
    test_size=0.2
)

# Tokenizacja danych
train_encodings_dziedzina = tokenizer(train_texts, truncation=True, padding=True)
val_encodings_dziedzina = tokenizer(val_texts, truncation=True, padding=True)

train_encodings_spec = tokenizer(train_texts_spec, truncation=True, padding=True)
val_encodings_spec = tokenizer(val_texts_spec, truncation=True, padding=True)

# Tworzenie datasetów
class QuestionDataset(torch.utils.data.Dataset):
    def __init__(self, encodings, labels):
        self.encodings = encodings
        self.labels = labels

    def __getitem__(self, idx):
        item = {key: torch.tensor(val[idx]) for key, val in self.encodings.items()}
        item['labels'] = torch.tensor(self.labels[idx])
        return item

    def __len__(self):
        return len(self.labels)

train_dataset_dziedzina = QuestionDataset(train_encodings_dziedzina, train_labels_dziedzina)
val_dataset_dziedzina = QuestionDataset(val_encodings_dziedzina, val_labels_dziedzina)

train_dataset_spec = QuestionDataset(train_encodings_spec, train_labels_spec)
val_dataset_spec = QuestionDataset(val_encodings_spec, val_labels_spec)

# Definicja parametrów treningu
training_args = TrainingArguments(
    output_dir='./results',                    # katalog do zapisu wyników
    num_train_epochs=30,                       # liczba epok
    per_device_train_batch_size=8,             # rozmiar batcha na urządzenie podczas treningu
    per_device_eval_batch_size=16,             # rozmiar batcha na urządzenie podczas ewaluacji
    warmup_steps=2000,                         # liczba kroków do rozgrzewki
    weight_decay=0.01,                         # współczynnik regularizacji
    logging_dir='./logs',                      # katalog do zapisu logów
    logging_steps=10,
    evaluation_strategy="epoch",               # ewaluacja po każdej epoce
    save_strategy="epoch",                     # zapis modelu po każdej epoce
    load_best_model_at_end=True,               # ładowanie najlepszego modelu na końcu
    learning_rate=3e-5,                        # współczynnik uczenia
    lr_scheduler_type="cosine_with_restarts",  # harmonogram uczenia
)

# Inicjalizacja trenera dla dziedzin
trainer_dziedzina = Trainer(
    model=model_dziedziny,                   # wytrenowany model
    args=training_args,                      # argumenty treningowe
    train_dataset=train_dataset_dziedzina,   # dataset treningowy
    eval_dataset=val_dataset_dziedzina       # dataset walidacyjny
)

# Trening modelu dla dziedzin
trainer_dziedzina.train()

# Inicjalizacja trenera dla specjalistyczności
trainer_spec = Trainer(
    model=model_specjalistyczne,                 # wytrenowany model
    args=training_args,                          # argumenty treningowe
    train_dataset=train_dataset_spec,            # dataset treningowy
    eval_dataset=val_dataset_spec                # dataset walidacyjny
)

# Trening modelu dla specjalistyczności
trainer_spec.train()

# Przygotowanie nowych pytań do przewidywania
new_encodings = tokenizer(new_questions['Pytanie'].tolist(), truncation=True, padding=True)
new_dataset = QuestionDataset(new_encodings, [0] * len(new_questions))  # Dummy labels

# Przewidywanie dziedzin nowych pytań
predictions_dziedzina = trainer_dziedzina.predict(new_dataset)
predicted_labels_dziedzina = torch.argmax(torch.tensor(predictions_dziedzina.predictions), axis=1)

# Przewidywanie specjalistyczności nowych pytań
predictions_spec = trainer_spec.predict(new_dataset)
predicted_labels_spec = torch.argmax(torch.tensor(predictions_spec.predictions), axis=1)

# Konwersja tensorów na listy
predicted_labels_dziedzina_list = predicted_labels_dziedzina.tolist()
predicted_labels_spec_list = predicted_labels_spec.tolist()

# Odwrócenie mapowania dziedzin
reverse_dziedziny_mapping = {v: k for k, v in dziedziny_mapping.items()}

# Dodanie przewidywanych dziedzin i specjalistyczności do DataFrame
new_questions['Dziedzina'] = [reverse_dziedziny_mapping[x] for x in predicted_labels_dziedzina_list]
new_questions['Specjalistyczne'] = ['tak' if x == 1 else 'nie' for x in predicted_labels_spec_list]

# Zapisanie wyników do nowego pliku CSV
output_file_path = 'predicted_questions.csv'
new_questions.to_csv(output_file_path, index=False)

print(f'Wyniki zapisano do pliku: {output_file_path}')
