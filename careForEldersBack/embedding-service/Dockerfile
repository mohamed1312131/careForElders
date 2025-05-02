# embedding-service/Dockerfile

# Use an official Python runtime as a parent image
FROM python:3.10-slim

# Set the working directory in the container
WORKDIR /app

# Copy the requirements file into the container at /app
COPY requirements.txt .

# Install any needed packages specified in requirements.txt
# --no-cache-dir reduces image size
# --upgrade pip ensures you have the latest pip
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application code into the container at /app
COPY . .

# Make port 8000 available to the world outside this container
EXPOSE 8000

# Define environment variable (optional, can be useful)
# ENV MODEL_NAME='all-MiniLM-L6-v2' # Example if you wanted to make model configurable

# Run uvicorn server when the container launches
# Use 0.0.0.0 to ensure it's accessible from outside the container
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]