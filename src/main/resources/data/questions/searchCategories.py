import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from transformers import BertTokenizer, BertForSequenceClassification, Trainer, TrainingArguments
from imblearn.over_sampling import RandomOverSampler
import torch
import numpy as np

file_path = 'questions-kopia.csv'
questions_df = pd.read_csv(file_path)

example_categories = ["Znaki Ostrzegawcze", "Znaki nakazu i zakazu", "Znaki informacyjne, kierunku",
                      "Znaki poziome", "Sygnalizacja świetlna", "Pierwszeństwo przejazdu",
                      "Zatrzymanie, postój, włączenie się do ruchu", "Zmiana kierunku jazdy, przejazdy kolejowe",
                      "Wyprzedzanie, omijanie, wymijanie cofanie", "Szczególna ostrożność",
                      "Warunki drogowe", "Prędkość, hamowanie, odstępy", "Technika jazdy",
                      "Obowiązki kierującego, dokumenty", "Awaria pojazdu, pierwsza pomoc", "Inne"]  # Added "Inne"

for i in range(3500):
    questions_df.at[i, 'Category'] = example_categories[i % len(example_categories)]

questions_df['Category'] = questions_df['Category'].fillna('Inne')

category_to_id = {category: idx for idx, category in enumerate(example_categories)}
id_to_category = {idx: category for category, idx in category_to_id.items()}

questions_df['Category_id'] = questions_df['Category'].map(category_to_id)

questions_df = questions_df.dropna(subset=['Category_id'])
questions_df['Category_id'] = questions_df['Category_id'].astype(int)

X = questions_df['0']
y = questions_df['Category_id']

ros = RandomOverSampler(random_state=42)
X_resampled, y_resampled = ros.fit_resample(X.values.reshape(-1, 1), y)
X_resampled = X_resampled.flatten()

print(pd.Series(y_resampled).describe())
print(pd.Series(y_resampled).unique())

X_train, X_test, y_train, y_test = train_test_split(X_resampled, y_resampled, test_size=0.2, random_state=42)

tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')

train_encodings = tokenizer(X_train.tolist(), truncation=True, padding=True, max_length=512)
test_encodings = tokenizer(X_test.tolist(), truncation=True, padding=True, max_length=512)

class TextDataset(torch.utils.data.Dataset):
    def __init__(self, encodings, labels):
        self.encodings = encodings
        self.labels = labels

    def __getitem__(self, idx):
        item = {key: torch.tensor(val[idx]) for key, val in self.encodings.items()}
        item['labels'] = torch.tensor(self.labels[idx], dtype=torch.long)
        return item

    def __len__(self):
        return len(self.labels)

train_dataset = TextDataset(train_encodings, y_train.tolist())
test_dataset = TextDataset(test_encodings, y_test.tolist())

model = BertForSequenceClassification.from_pretrained('bert-base-uncased', num_labels=len(category_to_id))

training_args = TrainingArguments(
    output_dir='./results',
    num_train_epochs=3,  
    per_device_train_batch_size=8,  
    per_device_eval_batch_size=8,
    warmup_steps=100,  
    weight_decay=0.01,
    logging_dir='./logs',
    evaluation_strategy="epoch",  
    save_total_limit=1,  
    learning_rate=5e-5,  
)

trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=train_dataset,
    eval_dataset=test_dataset,
    compute_metrics=lambda p: classification_report(p.label_ids, np.argmax(p.predictions, axis=1), output_dict=True)
)

trainer.train()

trainer.evaluate()

predictions = trainer.predict(test_dataset)
preds = np.argmax(predictions.predictions, axis=1)

classification_report_output = classification_report(y_test, preds, target_names=example_categories)

all_encodings = tokenizer(questions_df['0'].tolist(), truncation=True, padding=True, max_length=512)
all_dataset = TextDataset(all_encodings, questions_df['Category_id'].tolist())
all_predictions = trainer.predict(all_dataset)
questions_df['PredictedCategory'] = [id_to_category[i] for i in np.argmax(all_predictions.predictions, axis=1)]

output_file_path = 'kopia.csv'
questions_df.to_csv(output_file_path, index=False)

print(classification_report_output)
