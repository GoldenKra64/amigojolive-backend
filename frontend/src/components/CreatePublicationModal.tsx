import { useState } from 'react';
import Modal from './Modal';
import Field from './Field';
import Button from './Button';
import { createPublication } from '../api/publication.service';
import { useToast } from './Toast';

interface CreatePublicationModalProps {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export default function CreatePublicationModal({
  open,
  onClose,
  onSuccess,
}: CreatePublicationModalProps) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [isAnonymous, setIsAnonymous] = useState(false);
  const [loading, setLoading] = useState(false);
  const { success, error } = useToast();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim()) {
      error('El título y el contenido son obligatorios.');
      return;
    }

    setLoading(true);
    try {
      await createPublication({
        title: title.trim(),
        content: content.trim(),
        isAnonymous,
        tags: [],
      });
      success('Publicación creada correctamente.');
      setTitle('');
      setContent('');
      setIsAnonymous(false);
      onSuccess();
      onClose();
    } catch (err) {
      error('Error al crear la publicación.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Nueva Publicación"
      footer={
        <div className="flex justify-end gap-2">
          <Button variant="secondary" onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} loading={loading}>
            Publicar
          </Button>
        </div>
      }
    >
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <Field
          label="Título"
          placeholder="Escribe un título descriptivo"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          disabled={loading}
        />
        <Field
          as="textarea"
          label="Contenido"
          placeholder="¿Qué quieres compartir hoy?"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
          disabled={loading}
        />
        <label className="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            checked={isAnonymous}
            onChange={(e) => setIsAnonymous(e.target.checked)}
            className="h-4 w-4 rounded border-slate-300 text-brand-600 focus:ring-brand-500"
            disabled={loading}
          />
          <span className="text-sm text-slate-700">Publicar como anónimo</span>
        </label>
      </form>
    </Modal>
  );
}
