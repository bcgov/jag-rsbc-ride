# FROM registry.access.redhat.com/ubi8/python-39
FROM image-registry.openshift-image-registry.svc:5000/be5301-tools/python-3.9-ubi-base-image:1.0

# RUN addgroup --system app && adduser --system --group app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV ENVIRONMENT prod
ENV TESTING 0


WORKDIR /app
COPY src/ .
RUN pip install --no-cache-dir -r requirements.txt
# RUN chown -R app:app /app
# USER app
# CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "5000"]
CMD ["gunicorn", "main:app", "-w", "4", "-k", "uvicorn.workers.UvicornWorker", "-b", "0.0.0.0:5000", "--error-logfile", "-"]