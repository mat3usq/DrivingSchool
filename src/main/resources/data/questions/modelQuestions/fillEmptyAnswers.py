import pandas as pd

file_path = 'basic_questions.xlsx'
excel_data = pd.ExcelFile(file_path)

df = pd.read_excel(file_path, sheet_name='Treść pytania')

def update_answers_and_correct_response(row):
    if row['Poprawna odp'].strip().upper() in ['TAK', 'NIE']:
        row['Odpowiedź A'] = 'TAK'
        row['Odpowiedź B'] = 'NIE'
        row['Odpowiedź C'] = 'BRAK'
        row['Poprawna odp'] = row['Poprawna odp'].strip().upper()
    return row

df_updated = df.apply(update_answers_and_correct_response, axis=1)

output_file_path = 'questions.xlsx'
df_updated.to_excel(output_file_path, index=False)
