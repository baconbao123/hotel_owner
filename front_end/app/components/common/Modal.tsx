import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  title?: string;
  size?: 'small' | 'medium' | 'large' | 'full';
  showHeader?: boolean;
  showFooter?: boolean;
  onConfirm?: () => void;
  confirmLabel?: string;
  cancelLabel?: string;
  confirmIcon?: string;
  cancelIcon?: string;
  confirmSeverity?: 'primary' | 'secondary' | 'success' | 'info' | 'warning' | 'danger';
  draggable?: boolean;
  closeOnEscape?: boolean;
  dismissableMask?: boolean;
  position?: 'center' | 'top' | 'bottom' | 'left' | 'right' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
}

export function Modal({ 
  isOpen, 
  onClose, 
  children, 
  title,
  size = 'medium',
  showHeader = true,
  showFooter = false,
  onConfirm,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  confirmIcon = 'pi pi-check',
  cancelIcon = 'pi pi-times',
  confirmSeverity = 'primary',
  draggable = false,
  closeOnEscape = true,
  dismissableMask = true,
  position = 'center'
}: ModalProps) {

  const sizeMap = {
    small: '20vw',
    medium: '50vw',
    large: '70vw',
    full: '90vw'
  };

  const footer = showFooter && (
    <div className="flex justify-end gap-2">
      <Button
        label={cancelLabel}
        icon={cancelIcon}
        onClick={onClose}
        className="p-button-text"
      />
      {onConfirm && (
        <Button
          label={confirmLabel}
          icon={confirmIcon}
          onClick={onConfirm}
          className={`p-button-${confirmSeverity}`}
          autoFocus
        />
      )}
    </div>
  );

  return (
    <Dialog 
      visible={isOpen} 
      onHide={onClose}
      header={showHeader ? title : null}
      footer={footer}
      modal
      className="p-fluid"
      breakpoints={{ '960px': '75vw', '641px': '90vw' }}
      style={{ width: sizeMap[size] }}
      draggable={draggable}
      closeOnEscape={closeOnEscape}
      dismissableMask={dismissableMask}
      position={position}
    >
      {children}
    </Dialog>
  );
}